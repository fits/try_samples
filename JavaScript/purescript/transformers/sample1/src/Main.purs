module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console
import Control.Monad.Writer.Trans
import Data.Maybe
import Data.Tuple

type WriterMaybe = WriterT String Maybe Int

logNum :: Int -> Int -> WriterMaybe
logNum n x = WriterT $ return (Tuple (x + n) (show x ++ " "))

plus :: Int -> Int -> WriterMaybe
plus n x = lift $ return (x + n)

none :: WriterMaybe
none = WriterT Nothing

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
  print $ runWriterT (return 1 :: WriterMaybe)
  print $ runWriterT $ (return 1 :: WriterMaybe) >>= logNum 2 >>= logNum 3
  print $ runWriterT $ (return 1 :: WriterMaybe) >>= logNum 2 >>= plus 3

  print $ runWriterT $ none
  print $ runWriterT $ none >>= logNum 2 >>= plus 3

