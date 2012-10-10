
import Control.Applicative

main = do
	let f = (+) <$> (*2) <*> (+10)
	putStrLn $ show $ f 4

