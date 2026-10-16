package main

import (
	"context"
	"flag"
	"log"

	"terraform-provider-item/provider"

	"github.com/hashicorp/terraform-plugin-framework/providerserver"
)

func main() {
	var debug bool

	flag.BoolVar(&debug, "debug", false, "")
	flag.Parse()

	opts := providerserver.ServeOpts{
		Address: "example.com/fits/item",
		Debug:   debug,
	}

	err := providerserver.Serve(context.Background(), provider.New(), opts)

	if err != nil {
		log.Fatal(err)
	}
}
