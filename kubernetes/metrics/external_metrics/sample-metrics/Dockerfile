FROM golang:1.21-bullseye AS build

WORKDIR /app

COPY go.mod ./
COPY go.sum ./
RUN go mod download

COPY *.go ./

RUN go build -o mainapp

FROM debian:bullseye-slim

WORKDIR /app

RUN apt-get update && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/mainapp .

CMD ["./mainapp"]
