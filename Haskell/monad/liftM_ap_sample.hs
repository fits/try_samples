
import Control.Applicative
import Control.Monad

main = do
	print $ (+) <$> Just 3 <*> Just 5
	print $ (+) `liftM` Just 3 `ap` Just 5
