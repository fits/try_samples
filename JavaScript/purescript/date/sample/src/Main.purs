module Main where

import Prelude
import Control.Monad.Eff (Eff)
import Control.Monad.Eff.Console (CONSOLE, log)
import Data.Maybe (Maybe, fromMaybe)
import Data.Date (Date, canonicalDate)
import Data.Enum (toEnum)

data Event = Event { id :: String, date :: Date }

instance showEvent :: Show Event where
  show (Event e) = "Event(id = " <> e.id <> ", date = " <> (show e.date) <> ")"

date :: Int -> Int -> Int -> Maybe Date
date y m d = canonicalDate <$> toEnum y <*> toEnum m <*> toEnum d

get_date :: Int -> Int -> Int -> Date
get_date y m = date y m >>> fromMaybe bottom

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  let ev = Event { id: "a01", date: (get_date 2018 4 8) }
  log $ show ev
