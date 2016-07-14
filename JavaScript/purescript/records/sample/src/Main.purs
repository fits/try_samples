module Main where

import Prelude
import Control.Monad.Eff (Eff)
import Control.Monad.Eff.Console (CONSOLE, log)

data Item = Item { name :: String, num :: Int }

instance itemShow :: Show Item where
    show (Item r) = "Item (name:" <> r.name <> ", num:" <> (show r.num) <> ")"

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  let item1 = Item { name: "item1", num: 123 }
  log $ show item1
