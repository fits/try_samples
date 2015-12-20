module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console
import Data.Tuple

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  let t = Tuple 1 "sample"
  print t
