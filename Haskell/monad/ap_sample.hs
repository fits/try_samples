
import Control.Monad

main = do
	print $ Just (* 3) `ap` (Just 5)
