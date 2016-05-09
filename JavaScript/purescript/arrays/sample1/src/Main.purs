module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console
import Data.Array
import Data.Maybe
import Data.Foldable (foldr)
import Data.Traversable (sequence, traverse)

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  print [1, 3, 5]
  print $ 1 .. 5

  print $ map (* 2) $ 1 .. 5

  print $ map (+ 10) $ 1 : [2, 3]

  print $ [1, 2] <> [3, 4]
  print $ [1, 2] ++ [3, 4]

  print $ (1 .. 3) >>= \x -> [x * 10]
  print $ (1 .. 3) >>= ((* 10) >>> return)
  print $ (1 .. 3) >>= ((* 10) >>> singleton)

  print $ foldr (+) 0 (1 .. 5)

  print $ sequence $ Just (1 .. 3)
  print $ sequence [Just 1, Just 2]

  print $ traverse (\x -> Just (x + 3)) [1, 2]
  print $ traverse (Just <<< (+ 3)) [1, 2]
