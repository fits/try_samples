
import Control.Monad.Cont

sample :: Int -> Cont r Int
sample n = callCC $ \cc -> do
	when (odd n) $ do
		-- (1)
		cc n

	-- (2)
	return (n * 10)

main = do
	runCont (sample 1) print -- (1)
	runCont (sample 2) print -- (2)
	runCont (sample 3) print -- (1)
	runCont (sample 4) print -- (2)
