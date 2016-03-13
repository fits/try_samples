module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console

data D a = A a | B a

instance showD :: (Show a) => Show (D a) where
    show (A a) = "A " ++ show a
    show (B a) = "B " ++ show a

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
    print $ A "aaa"
    print $ B 10
