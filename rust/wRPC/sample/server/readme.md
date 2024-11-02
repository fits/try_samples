# build

```sh
cargo build --release --target wasm32-wasip2
```

# run

```sh
wrpc-wasmtime tcp serve ./target/wasm32-wasip2/release/sample_server.wasm
```