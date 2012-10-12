import Control.Applicative

main = do
	let f1 = (+) <$> (*2) <*> (+10)
	putStrLn $ show $ f1 4

	let f2 = (\a b c -> a + b + c) <$> (*2) <*> (+10) <*> (+5)
	putStrLn $ show $ f2 4

