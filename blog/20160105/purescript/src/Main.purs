module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console
import Data.Tuple

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  let t = Tuple "two" 2
  print t
  
  let r = t >>= \x -> Tuple " + 1" (x + 1)
  print r
