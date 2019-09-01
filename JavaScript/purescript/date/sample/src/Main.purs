module Main where

import Prelude

import Effect (Effect)
import Effect.Console (logShow)

import Data.Maybe (Maybe)
import Data.Date (Date, canonicalDate)
import Data.Enum (toEnum)

type EventId = String
data Event = Event { id :: EventId, date :: Date }

instance showEvent :: Show Event where
  show (Event e) = "Event(id = " <> e.id <> ", date = " <> (show e.date) <> ")"

date :: Int -> Int -> Int -> Maybe Date
date y m d = canonicalDate <$> toEnum y <*> toEnum m <*> toEnum d

event :: EventId -> Date -> Event
event id dt = Event { id: id, date: dt }

createEvent :: EventId -> Maybe Date -> Maybe Event
createEvent id = map (event id)

main :: Effect Unit
main = do
  let ev = createEvent "a01" (date 2019 9 1)
  logShow ev
