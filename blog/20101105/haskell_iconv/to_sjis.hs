import Codec.Text.IConv (convert)
import Codec.Binary.UTF8.String (encodeString)
import qualified Data.ByteString.Lazy.Char8 as C

main = do
	let msg = "UTF-8 から Shift_JIS への文字コード変換テスト"
	-- 内部文字を encodeString で UTF-8 化してから Shift_JIS に変換
	let sjisMsg = C.unpack $ convert "UTF-8" "Shift_JIS" $ C.pack $ encodeString msg

	-- UTF-8 で出力
	putStrLn $ encodeString msg
	-- Shift_JIS で出力
	putStrLn sjisMsg
