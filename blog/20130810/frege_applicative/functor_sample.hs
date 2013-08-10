main = do
	let f = fmap (*3) (+100)
	putStrLn $ show $ f 1
	print $ f 1
