#!/bin/sh

TARGET=wasm32-wasip2

wac compose \
    --dep sample:app=./app/target/$TARGET/release/app.wasm \
    --dep sample:cart=./cart/target/$TARGET/release/cart.wasm \
    --dep sample:item=./item/target/$TARGET/release/item.wasm \
    -o output_c.wasm \
    compose.wac
