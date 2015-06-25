
firstMessage :: String -> String
firstMessage s@(x:xs) = show x ++ " is first of " ++ show s

main = do
	putStrLn $ firstMessage "test sample"
