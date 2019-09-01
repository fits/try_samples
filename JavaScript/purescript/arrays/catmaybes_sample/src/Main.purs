module Main where

import Prelude

import Data.Array (catMaybes)
import Data.Maybe (Maybe(..))

import Effect (Effect)
import Effect.Console (logShow)

main :: Effect Unit
main = do
  -- [1, 2, 3]
  logShow $ catMaybes [Nothing, Just 1, Just 2, Nothing, Just 3, Nothing]
