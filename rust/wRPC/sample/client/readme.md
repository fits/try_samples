# build

```sh
cargo build --release --target wasm32-wasip2
```

# run

```sh
wrpc-wasmtime tcp run ./target/wasm32-wasip2/release/sample-client.wasm
```