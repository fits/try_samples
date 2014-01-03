
import System.Environment
import Data.Conduit
import qualified Data.Conduit.Binary as CB
import qualified Data.Conduit.List as CL
import qualified Data.ByteString.Char8 as B

main = do
	args <- getArgs

	x <- runResourceT $ CB.sourceFile (args !! 0) $$ CB.lines =$ do
		_ <- CL.drop 1
		CL.take 3

	mapM (B.putStrLn . B.cons('#')) x

	return ()
