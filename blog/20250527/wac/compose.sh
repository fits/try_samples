#!/bin/sh

TARGET=wasm32-wasip2

wac compose \
    --dep testapp:app=./app/target/$TARGET/release/app.wasm \
    --dep testapp:cart=./cart/target/$TARGET/release/cart.wasm \
    --dep testapp:item=./item/target/$TARGET/release/item.wasm \
    -o output_2.wasm \
    compose.wac
