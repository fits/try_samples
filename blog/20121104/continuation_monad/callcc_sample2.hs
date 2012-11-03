
import Control.Monad.Cont

sample :: Int -> Cont r Int
sample n = callCC $ \cc1 -> do
	when (odd n) $ do
		-- (1)
		cc1 n

	r <- callCC $ \cc2 -> do
		when (n < 4) $ do
			-- (2)
			cc2 $ n * 1000

		when (n == 4) $ do
			-- (3)
			cc1 $ n * 100

		-- (4)
		return $ n * 10

	-- (5)
	return $ r + 1

main = do
	runCont (sample 1) print -- (1)
	runCont (sample 2) print -- (2) (5)
	runCont (sample 3) print -- (1)
	runCont (sample 4) print -- (3)
	runCont (sample 5) print -- (1)
	runCont (sample 6) print -- (4) (5)
