
import Control.Applicative

main = do
	let f = (+) <$> (*2) <*> (+10)
	-- 22
	putStrLn $ show $ f 4

	let f2 = (+) <$> ( (+) <$> (*2) <*> (+10) ) <*> (+5)
	-- 31
	putStrLn $ show $ f2 4

	let f3 = (\a b c -> a + b + c) <$> (*2) <*> (+10) <*> (+5)
	-- 31
	putStrLn $ show $ f3 4

