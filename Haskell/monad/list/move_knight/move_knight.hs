import Control.Monad

type KnightPos = (Int, Int)

moveKnight :: KnightPos -> [KnightPos]
moveKnight (c, r) = do
	(c', r') <- [
		(c + 2, r - 1), (c + 2, r + 1),
		(c - 2, r - 1), (c - 2, r + 1),
		(c + 1, r - 2), (c + 1, r + 2),
		(c - 1, r - 2), (c - 1, r + 2)
		]
	guard (c' `elem` [1..8] && r' `elem` [1..8])
	return (c', r')

in3 :: KnightPos -> [KnightPos]
in3 start = do
	first <- moveKnight start
	second <- moveKnight first
	moveKnight second

canReachIn3 :: KnightPos -> KnightPos -> Bool
canReachIn3 start end = end `elem` in3 start

main = do
	putStrLn $ show $ moveKnight (6, 2)
	putStrLn $ show $ moveKnight (8, 1)

	putStrLn $ show $ in3 (6, 2)

	putStrLn $ show $ (6, 2) `canReachIn3` (6, 1)
	putStrLn $ show $ (6, 2) `canReachIn3` (7, 3)
