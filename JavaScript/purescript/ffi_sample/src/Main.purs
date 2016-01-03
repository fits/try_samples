module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console

import Sample.Math

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  print $ plus 3 2
