import Data.Monoid

lengthCompare :: String -> String -> Ordering
lengthCompare x y = (length x `compare` length y) `mappend` (x `compare` y)

main = do
	putStrLn $ show $ lengthCompare "zen" "ants"
	putStrLn $ show $ lengthCompare "zen" "ant"
