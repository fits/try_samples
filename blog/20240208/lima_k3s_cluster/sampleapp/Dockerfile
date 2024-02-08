FROM golang:1.21-bullseye AS build

WORKDIR /work
COPY main.go ./
RUN CGO_ENABLED=0 go build -o sampleapp main.go

FROM scratch

COPY --from=build /work/sampleapp /app/sampleapp
CMD ["/app/sampleapp"]
