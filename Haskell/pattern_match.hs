
showNum :: Int -> String
showNum 1 = "one"
showNum 2 = "two"
-- showNum n = show n
showNum _ = "etc"

main = do
	print $ map showNum [0..3]
