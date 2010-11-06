import System
import System.Time (CalendarTime(..), getClockTime, toCalendarTime)
import Network.Socket (SockAddr(..), inet_addr)
import Network.SMTP.Client
import Network.SMTP.Simple
import Codec.Text.IConv (convert)
import qualified Data.ByteString.Lazy.Char8 as C
import qualified Codec.Binary.Base64.String as B

sourceEncode = "Shift_JIS"
targetEncode = "UTF-8"

-- 文字コード変換
convertEncode :: String -> String
convertEncode s = C.unpack $ convert sourceEncode targetEncode $ C.pack $ s

--Base64エンコード
base64Encode :: String -> String
base64Encode s = B.encode $ convertEncode s

-- ヘッダー用Base64エンコード
base64Header :: String -> String
base64Header s = "=?" ++ targetEncode ++ "?B?" ++ (base64Encode s) ++ "?="

-- Mime用メッセージ変換
toMimeMessage :: CalendarTime -> SimpleMessage -> Message
toMimeMessage ct sm =
	Message
		[
			From (from sm), 
			To (to sm), 
			Subject (base64Header $ subject sm), 
			Date ct,
			OptionalField "Content-Type" ("text/plain; charset=" ++ targetEncode),
			OptionalField "Content-Transfer-Encoding" "BASE64"
		]
		(base64Encode $ body sm)

-- Mimeメッセージ送信
sendMimeMessage :: String -> SimpleMessage -> IO()
sendMimeMessage smtpHostIp msg = do
	nowCT <- toCalendarTime =<< getClockTime

	-- Fromメールアドレスからドメイン部分を取り出し
	let heloDomain = tail $ snd $ break (== '@') $ nameAddr_addr $ head $ from msg

	hostAddr <- inet_addr smtpHostIp
	let smtpSockAddr = SockAddrInet 25 hostAddr

	-- メール送信
	sendRawMessages putStr smtpSockAddr heloDomain [toMimeMessage nowCT msg]


-- メイン処理
main = do
	args <- getArgs
	-- メール本文（標準入力から取得）
	body <- getContents

	let msg = SimpleMessage [NameAddr Nothing (args !! 1)] [NameAddr Nothing (args !! 2)] (args !! 3) body

	sendMimeMessage (head args) msg

