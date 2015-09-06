
import Control.Monad.State

pop :: Num a => State [a] a
pop = do
	st <- get
	case st of
		[] -> return 0
		(x:xs) -> do
			put xs
			return x

sum3 :: Num a => State [a] a
sum3 = do
	x <- pop
	y <- pop
	z <- pop
	return (x + y + z)

main = do
	print $ runState pop [1, 2, 3, 4]
	print $ runState (pop >>= \_ -> pop) [1, 2, 3, 4]

	print $ runState sum3 [1, 2, 3, 4]
	print $ runState sum3 [1, 2]
	print $ runState sum3 [1]
	print $ runState sum3 []
