
-- (1) モナドとして扱う型を定義
newtype Counter a = Counter { getCount :: (a, Int) }

-- (2) Monad のインスタンスを定義
instance Monad Counter where
	return x = Counter (x, 0)
	(Counter (x, c)) >>= f = let (y, d) = getCount(f x) in Counter (y, c + d)

append :: String -> String -> Counter String
append s x = Counter (x ++ s, 1)

-- Counter モナドの利用
main = do
	-- ("a",0)
	print $ getCount $ return "a"

	-- ("ab",1)
	print $ getCount $ return "a" >>= append "b"
	print $ getCount $ append "b" "a"

	-- ("abc",2)
	print $ getCount $ return "a" >>= append "b" >>= append "c"

	-- ("d", 3)
	print $ getCount $ Counter ("d", 3) >>= return
