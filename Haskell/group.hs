
group :: Eq a => [a] -> [[a]]
group = foldr dist []

dist :: Eq a => a -> [[a]] -> [[a]]
dist x [] = [[x]]
dist x (xs:ys) = if (x == head xs) then (x:xs):ys else [x]:xs:ys

group2 :: Eq a => [a] -> [[a]]
group2 [] = []
group2 [x] = [[x]]
group2 (x:xs) = 
	let ys = group xs
	in if x == (head . head) ys then (x:head ys):(tail ys) else [x]:ys

group3 :: Eq a => [a] -> [[a]]
group3 [] = []
group3 [x] = [[x]]
group3 xs@(x:xs') = ys:group3 zs
	where (ys, zs) = span' (x ==) xs

span' :: (a -> Bool) -> [a] -> ([a], [a])
span' _ [] = ([], [])
span' p xs@(x:xs')
	| p x       = let (ys, zs) = span' p xs' in (x:ys, zs)
	| otherwise = ([], xs)

main = do
	print $ group [1, 1, 2, 3, 3, 3]
	print $ group ["A", "B", "B", "C", "A", "A", "B", "B"]

	print $ group2 [1, 1, 2, 3, 3, 3]
	print $ group2 ["A", "B", "B", "C", "A", "A", "B", "B"]

	print $ group3 [1, 1, 2, 3, 3, 3]
	print $ group3 ["A", "B", "B", "C", "A", "A", "B", "B"]
