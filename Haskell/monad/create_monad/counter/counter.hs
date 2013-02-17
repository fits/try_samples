
newtype Counter a = Counter { getCount :: (a, Int) }

instance Monad Counter where
	return x = Counter (x, 0)
	(Counter (a, c)) >>= f = let (b, d) = getCount(f a) in Counter (b, c + d)

countUp :: String -> String -> Counter String
countUp s x = Counter (x ++ s, 1)

main = do
	-- ("a", 0)
	print $ getCount $ return "a"

	-- ("ab", 1)
	print $ getCount $ return "a" >>= countUp "b"

	print $ getCount $ countUp "b" "a"

	-- ("abc", 2)
	print $ getCount $ return "a" >>= countUp "b" >>= countUp "c"
