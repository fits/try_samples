module Main where

import Debug.Trace

f1 = (+)10 <$> (*)3

main = do
	print $ f1 4
