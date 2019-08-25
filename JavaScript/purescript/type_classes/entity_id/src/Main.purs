module Main where

import Prelude

import Effect (Effect)
import Effect.Console (logShow)

newtype ItemId = ItemId String

data Item = Item { id :: ItemId, name :: String }

class (Eq i) <= Entity e i | e -> i where
  entityId :: e -> i

instance showItemId :: Show ItemId where
  show (ItemId a) = a

instance eqItemId :: Eq ItemId where
  eq (ItemId a) (ItemId b) = a == b

instance showItem :: Show Item where
  show (Item r) = "Item " <> show r

instance entityItem :: Entity Item ItemId where
  entityId (Item r) = r.id

main :: Effect Unit
main = do
  let item1 = Item { id: (ItemId "id1"), name: "item1" }
  
  logShow item1
  logShow $ entityId item1
