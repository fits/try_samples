import System

main = do
	cs <- getArgs
	print cs

	print (cs !! 1)
	print (cs !! 2)

