
import Control.Monad.Cont

sample :: Int -> Cont r Int
sample n = callCC $ \k1 -> do
	when (odd n) $ do
		-- (1)
		k1 n

	-- (2)
	return 0

main = do
	runCont (sample 1) print -- (1)
	runCont (sample 2) print -- (2)
	runCont (sample 3) print -- (1)
	runCont (sample 4) print -- (2)
