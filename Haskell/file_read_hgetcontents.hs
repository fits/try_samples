import System.Environment
import System.IO

main = do
	args <- getArgs
	handle <- openFile (head args) ReadMode

	contents <- hGetContents handle

	putStr contents

	hClose handle

