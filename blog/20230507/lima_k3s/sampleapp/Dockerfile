FROM rust:1.69 as builder

WORKDIR /app

COPY . .

RUN cargo install --path .

FROM debian:bullseye-slim

WORKDIR /app

RUN apt-get update && rm -rf /var/lib/apt/lists/*

COPY --from=builder /usr/local/cargo/bin/sampleapp .

CMD ["./sampleapp"]
