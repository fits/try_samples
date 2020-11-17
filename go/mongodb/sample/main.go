package main

import (
	"context"
	"fmt"
	"log"
	"math/rand"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type CreateItem struct {
	Name string
	Value int32
}

type Item struct {
	ID primitive.ObjectID `bson:"_id"`
	Name string
	Value int32
}

func insertItem(ctx context.Context, col *mongo.Collection, doc interface{}) (*primitive.ObjectID, error) {
	res, err := col.InsertOne(ctx, doc)

	if err != nil {
		return nil, err
	}

	fmt.Printf("insertedID = %#v\n", res.InsertedID)

	id := res.InsertedID.(primitive.ObjectID)

	return &id, nil
}

func findDocs(ctx context.Context, col *mongo.Collection, query interface{}) {
	cursor, err := col.Find(ctx, query)
	defer cursor.Close(ctx)

	if err != nil {
		log.Fatal(err)
	}

	var rs []bson.M
	err = cursor.All(ctx, &rs)

	if err != nil {
		log.Fatal(err)
	}

	for _, r := range rs {
		fmt.Printf("%#v\n", r)
	}
}

func findItems(ctx context.Context, col *mongo.Collection, query interface{}) {
	cursor, err := col.Find(ctx, query)
	defer cursor.Close(ctx)

	if err != nil {
		log.Fatal(err)
	}

	var rs []Item
	err = cursor.All(ctx, &rs)

	if err != nil {
		log.Fatal(err)
	}

	for _, r := range rs {
		fmt.Printf("%#v\n", r)
	}
}

func findItem(ctx context.Context, col *mongo.Collection, query interface{}) {
	var item Item
	col.FindOne(ctx, query).Decode(&item)

	fmt.Printf("%#v\n", item)
}

func main() {
	mongoUri := "mongodb://localhost"

	ctx := context.Background()

	opts := options.Client().ApplyURI(mongoUri)
	client, err := mongo.Connect(ctx, opts)

	if err != nil {
		println("error connect")
		panic(err)
	}

	defer func() {
		err = client.Disconnect(ctx)
		println("disconnect")

		if err != nil {
			panic(err)
		}
	}()

	col := client.Database("sample").Collection("items")

	r := rand.New(rand.NewSource(time.Now().UnixNano()))

	d1 := bson.M{"name": "item-1", "value": r.Int31n(100)}
	id1, err := insertItem(ctx, col, d1)

	if err != nil {
		panic(err)
	}

	findItem(ctx, col, bson.M{"_id": id1})

	d2 := CreateItem{Name: "item-2", Value: r.Int31n(100)}
	id2, err := insertItem(ctx, col, d2)

	if err != nil {
		panic(err)
	}

	findItem(ctx, col, bson.M{"_id": id2})

	println("-----")

	findDocs(ctx, col, bson.M{"value": bson.M{"$gte": 20}})

	println("-----")

	findItems(ctx, col, bson.M{"value": bson.M{"$gte": 50}})

	println("-----")

	findItem(ctx, col, bson.M{})
}
