
import Control.Monad.Cont

plus_cont :: Int -> Cont r Int
plus_cont x = return (3 + x)

calc_cont :: Int -> Cont r Int
calc_cont a = plus_cont a >>= (\x -> return (x * 2)) >>= (\y -> return (y + 1))

test :: a -> Cont r Int
test _ = return 5

main = do
	runCont (plus_cont 2) print
	runCont (calc_cont 2) print

	runCont (calc_cont 4) print

	runCont (test 1) (\x -> print x)

