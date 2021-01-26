package main

import (
	"context"
	"log"

	"go.opentelemetry.io/otel"
	"go.opentelemetry.io/otel/exporters/stdout"
	sdktrace "go.opentelemetry.io/otel/sdk/trace"
)

func initTracer() {
	exp, err := stdout.NewExporter(stdout.WithPrettyPrint())

	if err != nil {
		log.Fatal(err)
	}

	sp := sdktrace.NewSimpleSpanProcessor(exp)

	tp := sdktrace.NewTracerProvider(sdktrace.WithSpanProcessor(sp))

	otel.SetTracerProvider(tp)
}

func main() {
	initTracer()

	tp := otel.GetTracerProvider()
	t := tp.Tracer("sample1")

	_, s := t.Start(context.Background(), "span-1")

	log.Printf("created span = %#v", s)

	s.AddEvent("event-1")
	s.AddEvent("event-2")

	s.End()
}
