
main = do
	cs <- getContents
	putStr $ convert cs

convert :: String -> String
convert a = map translate a
	where
		translate :: Char -> Char
		translate 'a' = 'A'
		translate a = a
