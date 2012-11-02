
import Control.Monad.Cont

sample :: Int -> Cont r Int
sample n = callCC $ \k1 -> do
	when (odd n) $ do
		-- (1)
		k1 n

	r <- callCC $ \k2 -> do
		when (n < 4) $ do
			-- (2)
			k2 $ n * 100

		when (n > 6) $ do
			-- (3)
			k1 $ n * 10

		-- (4)
		return 0

	-- (5)
	return $ r + 1

main = do
	runCont (sample 1) print -- (1)
	runCont (sample 2) print -- (2) (4)
	runCont (sample 3) print -- (1)
	runCont (sample 4) print -- (4) (5)
	runCont (sample 5) print -- (1)
	runCont (sample 6) print -- (4) (5)
	runCont (sample 7) print -- (1)
	runCont (sample 8) print -- (3)

