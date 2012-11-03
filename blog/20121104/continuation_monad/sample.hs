
import Control.Monad.Cont

calc1 :: Int -> Cont r Int
calc1 x = return (x + 3)

calc2 :: Int -> Cont r Int
calc2 x = return (x * 10)

calc3 :: Int -> Cont r Int
calc3 x = return (x + 4)

calcAll :: Int -> Cont r Int
calcAll x = return x >>= calc1 >>= calc2 >>= calc3

main = do
	-- 2 + 3
	runCont (calc1 2) print

	-- ((2 + 3) * 10) + 4
	runCont (calcAll 2) print
	-- 上記は以下と同じ
	-- runCont (calcAll 2) (\x -> print x)

	print $ runCont (calcAll 2) (\x -> x - 9)

