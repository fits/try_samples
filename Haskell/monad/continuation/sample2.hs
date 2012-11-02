
import Control.Monad.Cont

sample :: Int -> Cont r Int
sample n = callCC $ \k -> do
	when (odd n) $ do
		k n

	return 0

main = do
	runCont (sample 1) print
	runCont (sample 2) print
	runCont (sample 3) print
	runCont (sample 4) print

