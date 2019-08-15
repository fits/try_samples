module Main where

import Prelude

import Effect (Effect)
import Effect.Console (logShow)

import Data.Maybe (Maybe(..))
import Control.Monad.Free (Free, liftF, foldFree)

type Quantity = Int
data Stock = Stock Quantity

instance showStock :: Show Stock where
  show (Stock qty) = "Stock " <> show qty

data Command a = Create a | Add Quantity a
type CommandOp a = Free Command a

createStock :: CommandOp Stock
createStock = liftF $ Create $ Stock 0

addStock :: Quantity -> Stock -> CommandOp Stock
addStock qty (Stock q) = liftF $ Add qty $ Stock (qty + q)

step :: Command ~> Maybe
step (Create s) = Just s
step (Add _ s) = Just s

interpret :: CommandOp Stock -> Maybe Stock
interpret = foldFree step

main :: Effect Unit
main = do
  logShow $ interpret $ createStock

  logShow $ interpret $ do
    a <- createStock
    b <- addStock 5 a
    addStock 2 b
