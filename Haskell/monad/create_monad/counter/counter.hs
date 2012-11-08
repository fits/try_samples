
newtype Counter a = Counter { getCount :: (a, Int) } deriving Show

instance Monad Counter where
	return x = Counter (x, 1)
	(Counter (a, c)) >>= f = let (b, _) = getCount(f a) in Counter (b, c + 1)

countUp :: String -> Counter String
countUp = return

main = do
	-- ("a", 3)
	print $ getCount $ countUp "a" >>= countUp >>= countUp
