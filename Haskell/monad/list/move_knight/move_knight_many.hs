import Control.Monad

type KnightPos = (Int, Int)

moveKnight :: KnightPos -> [KnightPos]
moveKnight (c, r) = filter onBoard
	[
		(c + 2, r - 1), (c + 2, r + 1),
		(c - 2, r - 1), (c - 2, r + 1),
		(c + 1, r - 2), (c + 1, r + 2),
		(c - 1, r - 2), (c - 1, r + 2)
	]
	where onBoard (c', r') = c' `elem` [1..8] && r' `elem` [1..8]

inMany :: Int -> KnightPos -> [KnightPos]
inMany x start = return start >>= foldr (<=<)
	return (replicate x moveKnight)

canReachIn :: Int -> KnightPos -> KnightPos -> Bool
canReachIn x start end = end `elem` inMany x start

main = do
	putStrLn $ show $ moveKnight (6, 2)
	putStrLn $ show $ moveKnight (8, 1)

	putStrLn $ show $ inMany 3 (6, 2)

	putStrLn $ show $ canReachIn 3 (6, 2) (6, 1)
	putStrLn $ show $ canReachIn 3 (6, 2) (7, 3)
