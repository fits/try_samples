package main

import (
	"context"
	"errors"
	"log"
	"net/http"

	"github.com/google/uuid"
	graphql "github.com/graph-gophers/graphql-go"
	"github.com/graph-gophers/graphql-go/relay"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"

	"sample/models"
	"sample/models/stocks"
	"sample/models/stockmoves"
)

const (
	address = ":4000"

	mongoUri = "mongodb://localhost"
	dbName = "stockmoves"
	colName = "events"
	stocksColName = "stocks"

	gqlSchema = `
		type StockMoveInfo {
			item: ID!
			qty: Int!
			from: ID!
			to: ID!
		}

		interface StockMove {
			id: ID!
			info: StockMoveInfo!
		}

		type DraftStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
		}

		type CompletedStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
			outgoing: Int!
			incoming: Int!
		}

		type CancelledStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
		}

		type AssignedStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
			assigned: Int!
		}

		type ShippedStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
			outgoing: Int!
		}

		type ArrivedStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
			outgoing: Int!
			incoming: Int!
		}

		type AssignFailedStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
		}

		type ShipmentFailedStockMove implements StockMove {
			id: ID!
			info: StockMoveInfo!
		}

		interface Stock {
			item: ID!
			location: ID!
		}

		type UnmanagedStock implements Stock {
			item: ID!
			location: ID!
		}

		type ManagedStock implements Stock {
			item: ID!
			location: ID!
			qty: Int!
			assigned: Int!
		}

		input CreateStockInput {
			item: ID!
			location: ID!
		}

		input StartMoveInput {
			item: ID!
			qty: Int!
			from: ID!
			to: ID!
		}

		type Query {
			findStock(item: ID!, location: ID!): Stock
			findMove(id: ID!): StockMove
		}

		type Mutation {
			createManaged(input: CreateStockInput!): ManagedStock
			createUnmanaged(input: CreateStockInput!): UnmanagedStock

			start(input: StartMoveInput!): StockMove
			assign(id: ID!): StockMove
			ship(id: ID!, outgoing: Int!): StockMove
			arrive(id: ID!, incoming: Int!): StockMove
			complete(id: ID!): StockMove
			cancel(id: ID!): StockMove
		}
	`
)

type MoveID = string
type Revision = uint32

type storedEvent struct {
	MoveID `bson:"move_id"`
	Revision
	Item models.Item
	Qty models.Quantity
	From models.Location
	To models.Location

	Started *models.Started `bson:",omitempty"`
	Completed *models.Completed `bson:",omitempty"`
	Cancelled *models.Cancelled `bson:",omitempty"`
	Assigned *models.Assigned `bson:",omitempty"`
	AssignShipped *models.AssignShipped `bson:",omitempty"`
	Shipped *models.Shipped `bson:",omitempty"`
	Arrived *models.Arrived `bson:",omitempty"`
}

func newStoredEvent(id MoveID, rev Revision, result *stockmoves.StockMoveResult) (*storedEvent, error) {
	if result == nil {
		return nil, errors.New("failed action")
	}
	
	info, ok := stockmoves.Info(result.State)

	if !ok {
		return nil, errors.New("invalid state")
	}

	se := storedEvent{
		MoveID: id, 
		Revision: rev, 
		Item: info.Item, 
		Qty: info.Qty, 
		From: info.From, 
		To: info.To,
	}

	switch ev := result.Event.(type) {
	case *models.Started:
		se.Started = ev
	case *models.Completed:
		se.Completed = ev
	case *models.Cancelled:
		se.Cancelled = ev
	case *models.Assigned:
		se.Assigned = ev
	case *models.AssignShipped:
		se.AssignShipped = ev
	case *models.Shipped:
		se.Shipped = ev
	case *models.Arrived:
		se.Arrived = ev
	}

	return &se, nil
}

func (e *storedEvent) Event() models.StockMoveEvent {
	if e.Started != nil {
		return e.Started
	} else if e.Completed != nil {
		return e.Completed
	} else if e.Cancelled != nil {
		return e.Cancelled
	} else if e.Assigned != nil {
		return e.Assigned
	} else if e.AssignShipped != nil {
		return e.AssignShipped
	} else if e.Shipped != nil {
		return e.Shipped
	} else if e.Arrived != nil {
		return e.Arrived
	}

	return nil
}

type store struct {
	context context.Context
	eventsCol *mongo.Collection
	stocksCol *mongo.Collection
}

func (s *store) saveStock(stock *stockResolver) error {
	opts := options.Update().SetUpsert(true)

	filter := bson.M{"_id": stock.ID}
	upd := bson.M{"$setOnInsert": stock}

	r, err := s.stocksCol.UpdateOne(s.context, filter, upd, opts)

	if err != nil {
		return err
	}

	if r.UpsertedCount == 0 {
		return errors.New("conflict stock")
	}

	return nil
}

func (s *store) loadStock(itemID graphql.ID, locationID graphql.ID) (*stockResolver, error) {
	item := models.Item(itemID)
	location := models.Location(locationID)

	query := bson.M{"_id": stockID(item, location)}

	var stockRes stockResolver
	err := s.stocksCol.FindOne(s.context, query).Decode(&stockRes)

	if err != nil {
		return nil, err
	}

	filter := bson.M{
		"$and": bson.A{
			bson.M{"item": item},
			bson.M{"$or": bson.A{
				bson.M{"from": location},
				bson.M{"to": location},
			},
		}},
	}

	cur, err := s.eventsCol.Find(s.context, filter)

	if err != nil {
		return nil, err
	}

	var events []storedEvent

	err = cur.All(s.context, &events)

	if err != nil {
		return nil, err
	}

	stock := stocks.RestoreStock(stockRes.Stock(), toMoveEvents(events))

	return newStockResolver(stock), nil
}

func (s *store) loadMove(id MoveID) (*stockMoveResolver, error) {
	opts := options.Find().SetSort(bson.M{"revision": 1})
	filter := bson.M{"move_id": id}

	cur, err := s.eventsCol.Find(s.context, filter, opts)

	if err != nil {
		return nil, err
	}

	var events []storedEvent
	err = cur.All(s.context, &events)

	if err != nil {
		return nil, err
	}

	fst := stockmoves.InitialState()
	lst := stockmoves.Restore(fst, toMoveEvents(events))

	if lst == fst {
		return nil, nil
	}

	return &stockMoveResolver{id, latestRevision(events), lst}, nil
}

func (s *store) saveEvent(event *storedEvent) error {
	if event == nil {
		return errors.New("event is nil")
	}

	opts := options.Update().SetUpsert(true)

	filter := bson.M{"move_id": event.MoveID, "revision": event.Revision}
	upd := bson.M{"$setOnInsert": event}

	r, err := s.eventsCol.UpdateOne(s.context, filter, upd, opts)

	if err != nil {
		return err
	}

	if r.UpsertedCount == 0 {
		return errors.New("conflict event revision")
	}

	return nil
}

func toMoveEvents(events []storedEvent) []models.StockMoveEvent {
	var es []models.StockMoveEvent

	for _, se := range events {
		ev := se.Event()

		if ev != nil {
			es = append(es, ev)
		}
	}

	return es
}

func latestRevision(events []storedEvent) Revision {
	var rev Revision

	for _, se := range events {
		if se.Revision > rev {
			rev = se.Revision
		}
	}

	return rev
}

type CreateStockInput struct {
	Item graphql.ID
	Location graphql.ID
}

type GqlUnmanagedStock interface {
	Item() graphql.ID
	Location() graphql.ID
}

type GqlManagedStock interface {
	Item() graphql.ID
	Location() graphql.ID
	Qty() int32
	Assigned() int32
}

type StartMoveInput struct {
	Item graphql.ID
	Qty int32
	From graphql.ID
	To graphql.ID
}

type GqlStockMoveInfo interface {
	Item() graphql.ID
	Qty() int32
	From() graphql.ID
	To() graphql.ID
}

type GqlStockMove interface {
	ID() graphql.ID
	Info() GqlStockMoveInfo
	Assigned() int32
	Outgoing() int32
	Incoming() int32
}

type stockResolver struct {
	ID        string                 `bson:"_id"`
	Unmanaged *stocks.UnmanagedStock `bson:",omitempty"`
	Managed   *stocks.ManagedStock   `bson:",omitempty"`
}

func stockID(item models.Item, location models.Location) string {
	return item + "/" + location
}

func newStockResolver(s stocks.Stock) *stockResolver {
	switch t := s.(type) {
	case *stocks.UnmanagedStock:
		return &stockResolver{ID: stockID(t.Item, t.Location), Unmanaged: t}
	case *stocks.ManagedStock:
		return &stockResolver{ID: stockID(t.Item, t.Location), Managed: t}
	}

	return nil
}

func (r *stockResolver) Stock() stocks.Stock {
	if r.Unmanaged != nil {
		return r.Unmanaged
	} else if r.Managed != nil {
		return r.Managed
	}

	return nil
}

func (r *stockResolver) Item() graphql.ID {
	var item string

	if r.Unmanaged != nil {
		item = r.Unmanaged.Item
	} else if r.Managed != nil {
		item = r.Managed.Item
	}
	return graphql.ID(item)
}

func (r *stockResolver) Location() graphql.ID {
	var location string

	if r.Unmanaged != nil {
		location = r.Unmanaged.Location
	} else if r.Managed != nil {
		location = r.Managed.Location
	}
	return graphql.ID(location)
}

func (r *stockResolver) Qty() int32 {
	if r.Managed != nil {
		return int32(r.Managed.Qty)
	}
	return 0
}

func (r *stockResolver) Assigned() int32 {
	if r.Managed != nil {
		return int32(r.Managed.Assigned)
	}
	return 0
}

func (r *stockResolver) ToUnmanagedStock() (GqlUnmanagedStock, bool) {
	if r.Unmanaged != nil {
		return r, true
	}

	return nil, false
}

func (r *stockResolver) ToManagedStock() (GqlManagedStock, bool) {
	if r.Managed != nil {
		return r, true
	}

	return nil, false
}

type stockMoveResolver struct {
	id string
	revision Revision
	state stockmoves.StockMove
}

func (r *stockMoveResolver) ID() graphql.ID {
	return graphql.ID(r.id)
}

func (r *stockMoveResolver) Info() GqlStockMoveInfo {
	return r
}

func (r *stockMoveResolver) Assigned() int32 {
	switch s := r.state.(type) {
	case stockmoves.AssignedStockMove:
		return int32(s.Assigned)
	}

	return 0
}

func (r *stockMoveResolver) Outgoing() int32 {
	switch s := r.state.(type) {
	case stockmoves.CompletedStockMove:
		return int32(s.Outgoing)
	case stockmoves.ShippedStockMove:
		return int32(s.Outgoing)
	case stockmoves.ArrivedStockMove:
		return int32(s.Outgoing)
	}

	return 0
}

func (r *stockMoveResolver) Incoming() int32 {
	switch s := r.state.(type) {
	case stockmoves.CompletedStockMove:
		return int32(s.Incoming)
	case stockmoves.ArrivedStockMove:
		return int32(s.Incoming)
	}

	return 0
}

func (r *stockMoveResolver) Item() graphql.ID {
	info, _ := stockmoves.Info(r.state)
	return graphql.ID(info.Item)
}

func (r *stockMoveResolver) Qty() int32 {
	info, _ := stockmoves.Info(r.state)
	return int32(info.Qty)
}

func (r *stockMoveResolver) From() graphql.ID {
	info, _ := stockmoves.Info(r.state)
	return graphql.ID(info.From)
}

func (r *stockMoveResolver) To() graphql.ID {
	info, _ := stockmoves.Info(r.state)
	return graphql.ID(info.To)
}

func (r *stockMoveResolver) ToDraftStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.DraftStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToCompletedStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.CompletedStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToCancelledStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.CancelledStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToAssignedStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.AssignedStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToShippedStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.ShippedStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToArrivedStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.ArrivedStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToAssignFailedStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.AssignFailedStockMove:
		return r, true
	}
	return nil, false
}

func (r *stockMoveResolver) ToShipmentFailedStockMove() (GqlStockMove, bool) {
	switch r.state.(type) {
	case stockmoves.ShipmentFailedStockMove:
		return r, true
	}
	return nil, false
}

type resolver struct {
	store
}

func (r *resolver) CreateUnmanaged(args struct { Input CreateStockInput }) (*stockResolver, error) {

	s := stocks.InitialUnmanaged(string(args.Input.Item), string(args.Input.Location))

	sr := newStockResolver(s)

	err := r.store.saveStock(sr)

	if err != nil {
		return nil, err
	}

	return sr, nil
}

func (r *resolver) CreateManaged(args struct { Input CreateStockInput }) (*stockResolver, error) {

	s := stocks.InitialManaged(string(args.Input.Item), string(args.Input.Location))

	sr := newStockResolver(s)

	err := r.store.saveStock(sr)

	if err != nil {
		return nil, err
	}

	return sr, nil
}

func (r *resolver) FindStock(args struct { Item graphql.ID; Location graphql.ID }) (*stockResolver, error) {

	return r.store.loadStock(args.Item, args.Location)
}

func (r *resolver) FindMove(args struct { ID graphql.ID }) (*stockMoveResolver, error) {
	return r.store.loadMove(string(args.ID))
}

type moveAction = func(state stockmoves.StockMove) *stockmoves.StockMoveResult 

func (r *resolver) doAction(cur *stockMoveResolver, action moveAction) (*stockMoveResolver, error) {
	if cur == nil {
		return nil, errors.New("not exists")
	}

	res := action(cur.state)

	revision := cur.revision + 1

	event, err := newStoredEvent(cur.id, revision, res)

	if err != nil {
		return nil, err
	}

	err = r.store.saveEvent(event)

	if err != nil {
		return nil, err
	}

	return &stockMoveResolver{cur.id, revision, res.State}, nil
}

func (r *resolver) Start(args struct { Input StartMoveInput }) (*stockMoveResolver, error) {
	id, err := uuid.NewRandom()

	if err != nil {
		return nil, err
	}

	cur := &stockMoveResolver{"mv-" + id.String(), 0, stockmoves.InitialState()}

	act := func(state stockmoves.StockMove) *stockmoves.StockMoveResult {
		return stockmoves.Start(
			state, 
			string(args.Input.Item), uint32(args.Input.Qty), 
			string(args.Input.From), string(args.Input.To),
		)
	}

	return r.doAction(cur, act)
}

func (r *resolver) Assign(args struct { ID graphql.ID }) (*stockMoveResolver, error) {
	cur, err := r.store.loadMove(MoveID(args.ID))

	if err != nil {
		return nil, err
	}

	stock, err := r.store.loadStock(cur.Item(), cur.From())

	if err != nil {
		return nil, err
	}

	act := func(state stockmoves.StockMove) *stockmoves.StockMoveResult {
		return stockmoves.Assign(state, stock.Stock())
	}

	return r.doAction(cur, act)
}

func (r *resolver) Ship(args struct { ID graphql.ID; Outgoing int32 }) (*stockMoveResolver, error) {
	cur, err := r.store.loadMove(MoveID(args.ID))

	if err != nil {
		return nil, err
	}

	act := func(state stockmoves.StockMove) *stockmoves.StockMoveResult {
		return stockmoves.Ship(state, models.Quantity(args.Outgoing))
	}

	return r.doAction(cur, act)
}

func (r *resolver) Arrive(args struct { ID graphql.ID; Incoming int32 }) (*stockMoveResolver, error) {
	cur, err := r.store.loadMove(MoveID(args.ID))

	if err != nil {
		return nil, err
	}

	act := func(state stockmoves.StockMove) *stockmoves.StockMoveResult {
		return stockmoves.Arrive(state, models.Quantity(args.Incoming))
	}

	return r.doAction(cur, act)
}

func (r *resolver) Complete(args struct { ID graphql.ID }) (*stockMoveResolver, error) {
	cur, err := r.store.loadMove(MoveID(args.ID))

	if err != nil {
		return nil, err
	}

	act := func(state stockmoves.StockMove) *stockmoves.StockMoveResult {
		return stockmoves.Complete(state)
	}

	return r.doAction(cur, act)
}

func (r *resolver) Cancel(args struct { ID graphql.ID }) (*stockMoveResolver, error) {
	cur, err := r.store.loadMove(MoveID(args.ID))

	if err != nil {
		return nil, err
	}

	act := func(state stockmoves.StockMove) *stockmoves.StockMoveResult {
		return stockmoves.Cancel(state)
	}

	return r.doAction(cur, act)
}

func main() {
	ctx := context.Background()

	opts := options.Client().ApplyURI(mongoUri)
	client, err := mongo.Connect(ctx, opts)

	if err != nil {
		log.Fatal(err)
	}

	defer client.Disconnect(ctx)

	store := store{
		ctx,
		client.Database(dbName).Collection(colName),
		client.Database(dbName).Collection(stocksColName),
	}

	gqlOpt := graphql.UseFieldResolvers()
	schema := graphql.MustParseSchema(gqlSchema, &resolver{ store }, gqlOpt)

	http.Handle("/", &relay.Handler{Schema: schema})

	err = http.ListenAndServe(address, nil)

	if err != nil {
		log.Printf("ERROR %v", err)
		return
	}
}
