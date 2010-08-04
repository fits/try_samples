import System
import Stack

sum :: Num a => Stack a -> a
sum stack = let (x1, stack') = pop stack
				(x2, _) = pop stack'
			in x1 + x2

main::IO()

main = do
	let num = sum Stack[1, 2, 3]
	putStrLn(show(num))

