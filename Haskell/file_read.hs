import System

main = do
	args <- getArgs
	contents <- readFile $ head args
	putStr contents

