module Main where

import Prelude

import Effect (Effect)
import Effect.Console (log)

main :: Effect Unit
main = do
  let d = [1, 2, 3]
  log $ show $ map ((*) 2) d
