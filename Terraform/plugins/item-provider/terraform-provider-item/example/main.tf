terraform {
  required_providers {
    item = {
      source = "example.com/fits/item"
    }
  }
}

provider "item" {
  endpoint = "http://127.0.0.1:8081/items"
}

resource "item_data" "item-1" {
  value = 35
}
