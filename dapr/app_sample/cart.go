package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"strings"
)

type Item struct {
	ID string `json:"id"`
	Price int
}

type Cart struct {
	ID string `json:"id"`
	Items []CartItem	
}

type CartItem struct {
	Item Item
	Qty int
}

type AddItem struct {
	ItemID string `json:"item_id"`
	Qty int
}

type State struct {
	Key string
	Value Cart
}

func serverError(w http.ResponseWriter, err error) {
	w.WriteHeader(http.StatusInternalServerError)
	fmt.Fprintf(w, "%v", err)
}

func findItem(endpoint string, itemID string) (Item, error) {
	res, err := http.Get(endpoint + "/" + itemID)

	var item Item

	if res.StatusCode != 200 {
		err = errors.New("failed to find item")
	}

	if err != nil {
		return item, err
	}

	err = json.NewDecoder(res.Body).Decode(&item)

	return item, err
}

func loadState(endpoint string, id string, handler func(io.Reader) error) error {
	res, err := http.Get(endpoint + "/" + id)

	if err != nil {
		return err
	}

	return handler(res.Body)
}

func saveState(endpoint string, data Cart) error {
	s, _ := json.Marshal([]State{
		State{data.ID, data},
	})

	body := bytes.NewBuffer(s)

	res, err := http.Post(endpoint, "application/json", body)

	if err != nil {
		return err
	}

	if res.StatusCode >= 300 {
		return errors.New(fmt.Sprintf("failed to store state: statusCode=%d", res.StatusCode))
	}

	return nil
}

func main() {
	daprPort := os.Getenv("DAPR_HTTP_PORT")
	port := os.Getenv("APP_HTTP_PORT")

	if port == "" {
		port = "3001"
	}

	itemAppId := os.Getenv("ITEM_APP_ID")

	if itemAppId == "" {
		itemAppId = "item"
	}

	daprUrl := fmt.Sprintf("http://localhost:%s/v1.0", daprPort)
	daprStateUrl := daprUrl + "/state/statestore"

	itemFindUrl := fmt.Sprintf("%s/invoke/%s/method/find", daprUrl, itemAppId)

	http.HandleFunc("/find/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/find/")

		err := loadState(daprStateUrl, id, func(b io.Reader) error {
			_, err := io.Copy(w, b)
			return err
		})

		if err != nil {
			serverError(w, err)
		}
	})

	http.HandleFunc("/additem/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/additem/")

		var param AddItem
		json.NewDecoder(r.Body).Decode(&param)

		if param.ItemID == "" || param.Qty <= 0 {
			serverError(w, errors.New("invalid request"))
			return
		}

		var cart Cart

		err := loadState(daprStateUrl, id, func(b io.Reader) error {
			json.NewDecoder(b).Decode(&cart)
			return nil
		})

		if err != nil {
			serverError(w, err)
			return
		}

		if cart.ID == "" {
			cart.ID = id
		}

		exists := false
		items := []CartItem{}

		for _, d := range cart.Items {
			if d.Item.ID == param.ItemID {
				items = append(items, CartItem{d.Item, d.Qty + param.Qty})
				exists = true
			} else {
				items = append(items, d)
			}
		}

		if !exists {
			item, err := findItem(itemFindUrl, param.ItemID)

			if err != nil {
				serverError(w, err)
				return
			}
			
			items = append(items, CartItem{item, param.Qty})
		}

		cart.Items = items

		err = saveState(daprStateUrl, cart)

		if err != nil {
			serverError(w, err)
			return
		}

		json.NewEncoder(w).Encode(cart)
	})

	log.Fatal(http.ListenAndServe(":" + port, nil))
}

