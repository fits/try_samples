
import Data.Conduit
import qualified Data.Conduit.List as CL

main = do
	x <- runResourceT $ CL.sourceList [1..10 :: Int] $$ CL.filter even =$ CL.consume

	putStrLn $ unlines $ map show x
