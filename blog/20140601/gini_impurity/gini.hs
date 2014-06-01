import Data.List

size = fromIntegral . length

-- (a) 1 - (AA + BB + CC)
giniA xs = (1 - ) . sum . map calc . group . sort $ xs
	where
		listSize = size xs
		calc x = (size x / listSize) ** 2

-- (b) AB + AC + BA + BC + CA + CB
giniB xs = sum . calcProb . map prob . group . sort $ xs
	where
		listSize = size xs
		prob ys = (head ys, size ys / listSize)
		calcProb zs = [ snd x * snd y | x <- zs, y <- zs, fst x /= fst y]

main = do
	let list = ["A", "B", "B", "C", "B", "A"]

	print $ giniA list
	print $ giniB list
