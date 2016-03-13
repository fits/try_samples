module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console
import Data.Yoneda

data D a = A a | B a

instance showD :: (Show a) => Show (D a) where
    show (A a) = "A " ++ show a
    show (B a) = "B " ++ show a

showYoneda :: forall f a. (Show a, Show (f a)) => Yoneda f a -> String
showYoneda m = show $ runYoneda m id

main :: forall e. Eff (console :: CONSOLE | e) Unit
main = do
    print $ A "aaa"
    print $ B "bbb"
    print $ showYoneda $ Yoneda (\k -> A (k 5))
    print $ showYoneda $ map show $ Yoneda (\k -> A (k 5))
