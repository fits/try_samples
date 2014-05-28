import Data.List

calcGini :: [String] -> Double
calcGini list = (1 - ) $ (foldl' prob 0) $ group $ sort list
	where
		size = fromIntegral . length
		listSize = size list
		prob acc x = acc + (size x / listSize) ** 2

main = do
	let list = ["A", "B", "B", "C", "B", "A"]
	print $ calcGini list
