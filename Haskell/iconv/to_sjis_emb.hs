import Codec.Text.IConv (convert)
import Codec.Binary.UTF8.String (encodeString)
import qualified Data.ByteString.Lazy.Char8 as C

main = do
	let msg = "テスト"
	-- ハードコーディングされた文字は encodeString で UTF-8 化する必要あり
	let sjisMsg = C.unpack $ convert "UTF-8" "Shift_JIS" $ C.pack $ encodeString msg

	print sjisMsg
	putStrLn sjisMsg
