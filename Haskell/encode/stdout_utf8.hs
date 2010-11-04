import System
import System.IO

main = do
	hSetEncoding stdout utf8

	putStrLn "テスト"
	hPutStrLn stdout "テスト"

