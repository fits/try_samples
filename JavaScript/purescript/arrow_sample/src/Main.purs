module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console

import Data.Profunctor
import Data.Profunctor.Strong
import Data.Tuple

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  let plus = arr $ (+) 3
  print $ plus 4
  
  let times = arr $ (*) 2
  print $ times 3
  
  let f = plus >>> times
  print $ f 4
  
  -- Tuple (3 + 3) (5 * 2) = Tuple (6) (10)
  print $ plus *** times $ Tuple 3 5
  print $ first plus >>> second times $ Tuple 3 5
  
  -- Tuple (4 + 3) (4 * 2) = Tuple (7) (8)
  print $ plus &&& times $ 4
