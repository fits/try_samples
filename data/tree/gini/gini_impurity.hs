import Data.List

countBy = map countItem . group . sort
	where
		countItem x = (head x, length x)

rate xs = map calcRate xs
	where
		size = fromIntegral . snd
		totalSize = foldl' (\acc x -> acc + size x) 0 xs
		calcRate x = (fst x, size x / totalSize)

calcGini1 = (1 - ) . (foldl' calc 0) . rate . countBy
	where
		calc acc x = acc + (snd x) ** 2

calcGini2 = foldl' sndSum 0 . combination . rate . countBy
	where
		sndSum acc x = acc + snd x
		combination list = [(fst x ++ fst y, snd x * snd y) | x <- list, y <- list, fst x /= fst y]


calcGini0 xs = (1 - ) . (foldl' calc 0) . group . sort $ xs
	where
		size = fromIntegral . length
		listSize = size xs
		calc acc x = acc + (size x / listSize) ** 2


main = do
	let list = ["A", "B", "B", "C", "B", "A"]
	print $ calcGini1 list
	print $ calcGini2 list

	print $ calcGini0 list

