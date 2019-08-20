module Main where

import Prelude

import Effect (Effect)
import Effect.Console (log, logShow)

data Item = Item { name :: String, num :: Int }

instance showItem :: Show Item where
  show (Item r) = "Item (name:" <> r.name <> ", num:" <> (show r.num) <> ")"

derive instance eqItem :: Eq Item

item :: String -> Int -> Item
item name num = Item { name: name, num: num }

showItemContents :: Item -> String
showItemContents (Item r) = show r

main :: Effect Unit 
main = do
  logShow $ item "sample1" 10
  log $ showItemContents $ item "sample2" 5
  
  let d1 = item "a1" 1
  let d2 = item "a2" 1
  
  logShow $ d1 == d2
  logShow $ d1 == (item "a1" 1)
  logShow $ d1 == (item "a1" 2)
