module Main where

import Prelude

import Control.Monad.Except (runExcept)

import Data.Generic.Rep (class Generic)
import Data.Generic.Rep.Show (genericShow)

import Data.Traversable (traverse)

import Effect (Effect)
import Effect.Console (logShow)

import Foreign (Foreign, F, readArray)
import Foreign.Generic (defaultOptions, genericDecode)

import Sample (createData, dataList)

newtype Data = Data {name :: String, value :: Int}

derive instance genericData :: Generic Data _

instance showData :: Show Data where
  show = genericShow

toData :: Foreign -> F Data
toData = genericDecode defaultOptions { unwrapSingleConstructors = true }

main :: Effect Unit
main = do
  let d = createData "sample"
  
  logShow $ runExcept $ toData d
  logShow $ runExcept $ traverse toData =<< readArray dataList
