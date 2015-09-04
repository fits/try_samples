
import Control.Monad.State

pop :: State [Int] Int
pop = do
	st <- get
	case st of
		[] -> return 0
		(x:xs) -> do
			put xs
			return x

sum3 :: State [Int] Int
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
