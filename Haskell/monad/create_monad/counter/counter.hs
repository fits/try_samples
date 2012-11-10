
newtype Counter a = Counter { getCount :: (a, Int) }

instance Monad Counter where
	return x = Counter (x, 1)
	(Counter (a, c)) >>= f = let (b, _) = getCount(f a) in Counter (b, c + 1)

countUp :: String -> String -> Counter String
countUp s x = return (x ++ s)

main = do
	-- ("a", 1)
	print $ getCount $ return "a"

	-- ("ab", 2)
	print $ getCount $ return "a" >>= countUp "b"

	-- ("abc", 3)
	print $ getCount $ return "a" >>= countUp "b" >>= countUp "c"
