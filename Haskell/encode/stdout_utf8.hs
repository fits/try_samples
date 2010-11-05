import System
import System.IO
import Codec.Binary.UTF8.String (encodeString, decodeString)
import Data.ByteString.Lazy.Char8 (pack, unpack)

main = do
	-- sjis <- mkTextEncoding "CP932"
	-- hSetEncoding stdin utf8

	hSetEncoding stdout utf8

	print "テスト"
	putStrLn "テスト"
	hPutStrLn stdout "テスト"

	input <- getContents
	print input
	putStrLn input
	hPutStrLn stdout input

	args <- getArgs
	print $ head args
	putStrLn $ head args
	hPutStrLn stdout $ head args

	print $ decodeString $ head args
	putStrLn $ encodeString $ head args
	
