module Main where

import Prelude
import Control.Monad.Eff (Eff)
import Control.Monad.Eff.Console (CONSOLE, log)
import Data.Tuple (Tuple(..))
import Data.Maybe (Maybe(..))

data Item = Item { name :: String, value :: Int }

instance showItem :: Show Item where
    show (Item r) = "Item(name=" <> r.name <> ", value=" <> (show r.value) <> ")"

item :: String -> Int -> Item
item s v = Item { name: s, value: v }

rename :: String -> Item -> Item
rename s (Item r) = item s r.value

split :: Int -> Item -> Tuple Item (Maybe Item)
split i p@(Item r)
    | i <= 0 || i > r.value  = Tuple p Nothing
    | otherwise              = Tuple (newItem (r.value - i)) (Just (newItem i))
        where
            newItem v = item r.name v

splitWithRename :: Int -> String -> Item -> Tuple Item (Maybe Item)
splitWithRename i s p = map liftRename (split i p)
    where
        liftRename = map (rename s)


main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
    let item1 = item "item1" 10

    log $ show $ split 7 item1

    log "----------"

    log $ show $ split 11 item1

    log "----------"

    log $ show $ rename "item1-upd" item1

    log "----------"

    log $ show $ splitWithRename 7 "item2" item1

    log "----------"

    log $ show $ splitWithRename 10 "item2" item1

    log "----------"

    log $ show $ splitWithRename 11 "item2" item1
