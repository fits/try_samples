import Control.Monad.Instances

f1 :: Int -> Int
f1 = do
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
	putStrLn $ show $ f1 4
	putStrLn $ show $ f2 4
