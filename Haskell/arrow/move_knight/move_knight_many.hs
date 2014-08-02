import Control.Arrow

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

inMany :: Int -> Kleisli [] KnightPos KnightPos
inMany x = foldr (>>>) returnA (replicate x (Kleisli moveKnight))

canReachInMany :: Int -> KnightPos -> KnightPos -> Bool
canReachInMany x end = runKleisli (inMany x) >>> elem end

main = do
	print $ runKleisli (inMany 3) $ (6, 2)

	print $ canReachInMany 3 (6, 1) $ (6, 2)
	print $ canReachInMany 3 (7, 3) $ (6, 2)
