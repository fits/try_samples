import System

main = do
	args <- getArgs

	-- ドメイン部分の取得
	putStrLn $ tail $ snd $ break (== '@') $ head args

	-- ドメイン部分の取得
	let domain = parseDomain $ head args
		where
			parseDomain :: String -> String
			parseDomain addr = tail $ snd $ break (== '@') $ addr

	putStrLn(domain)

