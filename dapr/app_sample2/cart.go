package main

import (
	"bytes"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
)

type Cart struct {
	ID string `json:"id"`
	Items []CartItem	
}

type CartItem struct {
	ItemID string `json:"item_id"`
	Price int
	Qty int
}

type AddItem struct {
	ItemID string `json:"item_id"`
	Qty int
}

type State struct {
	Key string
	Value Cart
	Etag *string
}

func serverError(w http.ResponseWriter, err error) {
	w.WriteHeader(http.StatusInternalServerError)
	fmt.Fprintf(w, "%v", err)
}

func findPrice(endpoint string, itemID string) (int, error) {
	res, err := http.Get(endpoint + "/" + itemID)

	var price int

	if res.StatusCode != 200 {
		err = errors.New("failed to get item price")
	}

	if err != nil {
		return price, err
	}

	err = json.NewDecoder(res.Body).Decode(&price)

	return price, err
}

func loadState(endpoint string, id string) (Cart, *string, error) {
	res, err := http.Get(endpoint + "/" + id)

	var state Cart

	if err != nil {
		return state, nil, err
	}

	err = json.NewDecoder(res.Body).Decode(&state)

	if err == nil && state.ID == "" {
		err = errors.New(fmt.Sprintf("not found: id=%s", id))
	}

	etag := res.Header.Get("ETag")

	return state, &etag, err

}

func saveState(endpoint string, state Cart, etag *string) error {
	s, _ := json.Marshal([]State{
		State{state.ID, state, etag},
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

	itemPriceUrl := fmt.Sprintf("%s/invoke/%s/method/price", daprUrl, itemAppId)

	http.HandleFunc("/items/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/items/")

		state, _, err := loadState(daprStateUrl, id)

		if err != nil {
			serverError(w, err)
		}

		json.NewEncoder(w).Encode(state.Items)
	})

	http.HandleFunc("/additem/", func(w http.ResponseWriter, r *http.Request) {
		id := strings.TrimPrefix(r.URL.Path, "/additem/")

		var param AddItem
		json.NewDecoder(r.Body).Decode(&param)

		if param.ItemID == "" || param.Qty <= 0 {
			serverError(w, errors.New("invalid request"))
			return
		}

		cart, etag, _ := loadState(daprStateUrl, id)

		if cart.ID == "" {
			cart.ID = id
		}

		upd := false

		for i, d := range cart.Items {
			if d.ItemID == param.ItemID {
				cart.Items[i].Qty += param.Qty
				upd = true
			}
		}

		if !upd {
			price, err := findPrice(itemPriceUrl, param.ItemID)

			if err != nil {
				serverError(w, err)
				return
			}
			
			cart.Items = append(cart.Items, CartItem{param.ItemID, price, param.Qty})
		}

		err := saveState(daprStateUrl, cart, etag)

		if err != nil {
			serverError(w, err)
			return
		}

		json.NewEncoder(w).Encode(cart.Items)
	})

	log.Fatal(http.ListenAndServe(":" + port, nil))
}

