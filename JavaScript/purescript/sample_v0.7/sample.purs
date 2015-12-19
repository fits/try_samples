module Main where

import Prelude
import Control.Monad.Eff
import Control.Monad.Eff.Console

main :: Eff (console :: CONSOLE) Unit
main = do
	let d = [1, 3, 5]
	let r = d >>= \x -> [x * x]
	print r
