terraform {
  required_providers {
    simple = {
      source = "example.com/fits/simple"
    }
  }
}

provider "simple" {}

data "simple_custom" "d1" {
  id = "data-1"
}
