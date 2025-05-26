#!/bin/sh

TARGET=wasm32-wasip2

wac plug ./cart/target/$TARGET/release/cart.wasm --plug ./item/target/$TARGET/release/item.wasm -o cart_p.wasm
wac plug ./app/target/$TARGET/release/app.wasm --plug cart_p.wasm -o output_1.wasm

rm cart_p.wasm
