module Main where

import Prelude

import Effect.Console (log)

import HTTPure as HTTPure

main :: HTTPure.ServerM
main = do
  HTTPure.serve 8080 router $ log "started"
  where
    router { method: HTTPure.Post } = HTTPure.ok "receive post"
    router { method: HTTPure.Get } = HTTPure.ok "receive get"
    router _ = HTTPure.ok "other"
