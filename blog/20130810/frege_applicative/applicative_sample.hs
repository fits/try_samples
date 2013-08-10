import Control.Applicative

main = do
	let f = (+) <$> (*3) <*> (+10)
	print $ f 4
