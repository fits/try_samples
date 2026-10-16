package provider

import (
	"testing"
)

func newClient() ItemClient {
	return ItemClient{DEFAULT_ENDPOINT}
}

func TestCreate(t *testing.T) {
	client := newClient()

	item, err := client.Create(1234)

	if err != nil {
		t.Fatal(err)
	}

	if item.Value != 1234 {
		t.Error("invalid value")
	}

	t.Logf("created: %v", item)
}

func TestDelete(t *testing.T) {
	client := newClient()

	item, err := client.Create(1234)

	if err != nil {
		t.Fatal(err)
	}

	err = client.Delete(item.Id)

	if err != nil {
		t.Fatal(err)
	}
}

func TestFind(t *testing.T) {
	client := newClient()

	item, err := client.Create(345)

	if err != nil {
		t.Fatal(err)
	}

	item2, err := client.Find(item.Id)

	if err != nil {
		t.Fatal(err)
	}

	if item.Id != item2.Id || item.Value != item2.Value {
		t.Errorf("%v is not equals %v", item, item2)
	}
}

func TestFindWithInvalidId(t *testing.T) {
	client := newClient()

	item, err := client.Find("invalid-item-id")

	if err == nil {
		t.Errorf("invalid item: %v", item)
	}
}

func TestUpdate(t *testing.T) {
	client := newClient()

	item, err := client.Create(1234)

	if err != nil {
		t.Fatal(err)
	}

	item2, err := client.Update(item.Id, item.Value+15)

	if err != nil {
		t.Fatal(err)
	}

	if item2.Value != 1249 {
		t.Errorf("failed update: %v", item2)
	}

	t.Logf("updated: before=%v, after=%v", item, item2)
}
