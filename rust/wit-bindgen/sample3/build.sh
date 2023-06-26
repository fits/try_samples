#!/bin/sh

cd item

cargo build --release --target wasm32-unknown-unknown
wasm-tools component new ./target/wasm32-unknown-unknown/release/item.wasm -o item-comp.wasm

cd ../cart

cargo build --release --target wasm32-unknown-unknown
wasm-tools component new ./target/wasm32-unknown-unknown/release/cart.wasm -o cart-comp.wasm
