#!/bin/sh

TARGET=wasm32-wasip2

wac plug ./cart/target/$TARGET/release/cart.wasm --plug ./item/target/$TARGET/release/item.wasm -o cart_tmp.wasm
wac plug ./app/target/$TARGET/release/app.wasm --plug cart_tmp.wasm -o output_p.wasm

rm cart_tmp.wasm
