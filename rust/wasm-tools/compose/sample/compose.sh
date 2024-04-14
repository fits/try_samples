#!/bin/sh

wasm-tools compose -c config.yaml -o compose.wasm app/target/wasm32-unknown-unknown/release/app.wasm
