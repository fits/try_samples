
import Control.Monad.Cont

sample :: Int -> Cont r Int
sample n = callCC $ \k1 -> do
	when (odd n) $ do
		k1 n

	r <- callCC $ \k2 -> do
		when (n < 4) $ do
			k2 $ n * 100

		when (n < 6) $ do
			k1 $ n * 10

		return 0

	return $ r + 1

main = do
	runCont (sample 1) print
	runCont (sample 2) print
	runCont (sample 3) print
	runCont (sample 4) print
	runCont (sample 5) print
	runCont (sample 6) print
	runCont (sample 7) print

