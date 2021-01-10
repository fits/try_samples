package main

import (
	"log"
	"net/http"
	"sync"

	restful "github.com/emicklei/go-restful/v3"
	"github.com/google/uuid"
)

type ItemInput struct {
	Value int32 `json:"value"`
}

type Item struct {
	ID    string `json:"id"`
	Value int32  `json:"value"`
}

type store struct {
	sync.RWMutex
	items map[string]*Item
}

func NewStore() *store {
	return &store{items: make(map[string]*Item)}
}

func (s *store) addItem(i *Item) bool {
	if i == nil {
		return false
	}

	s.Lock()
	defer s.Unlock()

	if _, ok := s.items[i.ID]; ok {
		return false
	}

	s.items[i.ID] = i

	return true
}

func (s *store) findItem(id string) *Item {
	s.RLock()
	defer s.RUnlock()

	return s.items[id]
}

func (s *store) allItems() []*Item {
	s.RLock()
	defer s.RUnlock()

	vs := make([]*Item, 0, len(s.items))

	for _, v := range s.items {
		vs = append(vs, v)
	}

	return vs
}

type resource struct {
	*store
}

func (r *resource) allItems(req *restful.Request, res *restful.Response) {
	err := res.WriteEntity(r.store.allItems())

	if err != nil {
		log.Printf("ERROR: %#v", err)
		res.WriteHeader(http.StatusInternalServerError)
	}
}

func (r *resource) findItem(req *restful.Request, res *restful.Response) {
	id := req.PathParameter("id")
	item := r.store.findItem(id)

	if item == nil {
		res.WriteHeader(http.StatusNotFound)
	} else {
		err := res.WriteEntity(item)

		if err != nil {
			log.Printf("ERROR: %#v", err)
			res.WriteHeader(http.StatusInternalServerError)
		}
	}
}

func (r *resource) createItem(req *restful.Request, res *restful.Response) {
	input := ItemInput{}
	err := req.ReadEntity(&input)

	if err != nil {
		logWarn(res.WriteError(http.StatusBadRequest, err))
		return
	}

	log.Printf("post: %#v", input)

	id, err := uuid.NewRandom()

	if err != nil {
		logWarn(res.WriteError(http.StatusInternalServerError, err))
		return
	}

	item := Item{id.String(), input.Value}

	if r.store.addItem(&item) {
		err = res.WriteHeaderAndEntity(http.StatusCreated, item)

		if err != nil {
			log.Printf("ERROR: %#v", err)
			res.WriteHeader(http.StatusInternalServerError)
		}
	} else {
		res.WriteHeader(http.StatusConflict)
	}
}

func logWarn(err error) {
	if err != nil {
		log.Printf("WARN: %#v", err)
	}
}

func main() {
	r := resource{NewStore()}
	ws := new(restful.WebService)

	ws.Path("/items").Consumes(restful.MIME_JSON).Produces(restful.MIME_JSON)

	ws.Route(ws.POST("").To(r.createItem))
	ws.Route(ws.GET("").To(r.allItems))
	ws.Route(ws.GET("/{id}").To(r.findItem))

	restful.Add(ws)

	err := http.ListenAndServe(":8080", nil)

	if err != nil {
		log.Fatal(err)
	}
}
