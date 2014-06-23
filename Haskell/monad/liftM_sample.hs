
import Control.Monad

main = do
	print $ liftM (* 3) (Just 5)
