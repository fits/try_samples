import System
import Codec.Text.IConv (convert)
import qualified Data.ByteString.Lazy.Char8 as C

main = do
	args <- getArgs
	let msg = C.unpack $ convert "Shift_JIS" "UTF-8" $ C.pack $ head args

	print msg
	putStrLn msg
