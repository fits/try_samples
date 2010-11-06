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
encodeBase64 :: String -> String
encodeBase64 s = B.encode $ convertEncode s

-- ヘッダー用Base64エンコード
encodeBase64Header :: String -> String
encodeBase64Header s = unlines $ map addEncodeInfo $ lines $ encodeBase64 s

-- エンコード情報を付与
addEncodeInfo :: String -> String
addEncodeInfo s = "=?" ++ targetEncode ++ "?B?" ++ s ++ "?="

-- Mime用メッセージ変換
toMimeMessage :: CalendarTime -> SimpleMessage -> Message
toMimeMessage ct sm =
	Message
		[
			From (from sm), 
			To (to sm), 
			Subject (encodeBase64Header $ subject sm), 
			Date ct,
			OptionalField "Content-Type" ("text/plain; charset=" ++ targetEncode),
			OptionalField "Content-Transfer-Encoding" "BASE64"
		]
		(encodeBase64 $ body sm)

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

