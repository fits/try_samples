import Codec.Text.IConv (convert)
import Data.ByteString.Lazy as L

main = L.interact $ convert "UTF-8" "Shift_JIS"
