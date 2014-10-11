module Main where

import Debug.Trace

add :: Number -> Number -> Number
add x y = x + y

main = do
	let num = add 3 6
	print $ "result = " ++ show(num)
