
import Control.Monad.Instances

f :: Int -> Int
f = do
	a <- (*2)
	b <- (+10)
	return (a + b)

f2 :: Int -> Int
f2 = do
	a <- (*2)
	b <- (+10)
	c <- (+5)
	return (a + b + c)

main = do
	putStrLn $ show $ f 4
	putStrLn $ show $ f2 4

