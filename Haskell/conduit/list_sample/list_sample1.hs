
import Data.Conduit
import qualified Data.Conduit.List as CL

main = do
	x <- CL.sourceList [1..10 :: Int] $$ CL.filter even =$ CL.consume

	putStrLn $ show x

	x2 <- CL.sourceList [1..10 :: Int] $$ CL.filter even =$ CL.take 3

	putStrLn $ show x2
