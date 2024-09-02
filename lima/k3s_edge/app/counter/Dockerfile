FROM golang:latest AS build

WORKDIR /work

COPY go.mod ./
RUN go mod download

COPY *.go ./

RUN CGO_ENABLED=0 go build -o mainapp

FROM scratch

COPY --from=build /work/mainapp /app/mainapp
CMD ["/app/mainapp"]
