module Main where

import Prelude

import Control.Monad.Except (runExcept)
import Data.Traversable (traverse)

import Effect (Effect)
import Effect.Class (liftEffect)
import Effect.Console (logShow)
import Effect.Aff (launchAff_)

import Foreign (Foreign, readArray, readString, readInt, F)
import Foreign.Index ((!))

import SQLite3 (closeDB, newDB, queryDB)

type Data = {name :: String, value :: Int}

toData :: Foreign -> F Data
toData v = do
  name <- v ! "name" >>= readString
  value <- v ! "value" >>= readInt
  pure {name, value}

main :: Effect Unit
main = launchAff_ do
  let file = "./sample.db"

  db <- newDB file

  _ <- queryDB db """
        CREATE TABLE IF NOT EXISTS data (name TEXT NOT NULL, value INTEGER NOT NULL)
      """ []

  _ <- queryDB db "INSERT INTO data values ('sample1', 1)" []
  _ <- queryDB db "INSERT INTO data values ('sample2', 2)" []
  _ <- queryDB db "INSERT INTO data values ('sample3', 3)" []

  r <- readArray <$> queryDB db "select name, value from data" []
  _ <- liftEffect $ logShow $ runExcept $ r >>= traverse toData
  
  closeDB db
