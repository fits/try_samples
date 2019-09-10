module Main where

import Prelude

import Control.Monad.Except (runExcept)
import Data.Traversable (traverse)

import Effect (Effect)
import Effect.Console (log, logShow)

import Foreign (Foreign, F, tagOf, typeOf, unsafeFromForeign, 
                readString, readInt, readArray)
import Foreign.Index ((!))

import Sample (createData, dataList)

type Data = {name :: String, value :: Int}
type NameOnlyData = {name :: String}

logDataString :: String -> Effect Unit
logDataString a = log a

logData :: Data -> Effect Unit
logData {name: a, value: b} = log $ a <> ", " <> show b

toUnsafeData :: Foreign -> Data
toUnsafeData = unsafeFromForeign

toUnsafeNameOnly :: Foreign -> NameOnlyData
toUnsafeNameOnly = unsafeFromForeign

toData :: Foreign -> F Data
toData v = do
  name <- v ! "name" >>= readString
  value <- v ! "value" >>= readInt
  pure { name, value }

main :: Effect Unit
main = do
  let d = createData "sample"
  log $ tagOf d
  log $ typeOf d
  log $ unsafeFromForeign d
  logDataString $ unsafeFromForeign d
  logData $ unsafeFromForeign d
  logShow $ toUnsafeData d
  logShow $ toUnsafeNameOnly d
  logShow $ runExcept $ toData d
  logShow $ runExcept $ traverse toData =<< readArray dataList
