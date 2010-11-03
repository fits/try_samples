import System
import Network.SMTP.Simple

main = do
	args <- getArgs
	-- メール本文（標準入力から取得）
	body <- getContents

	-- Fromメールアドレスからドメイン部分を取り出し
	let domain = tail $ snd $ break (== '@') $ args !! 1

	let msg = SimpleMessage [NameAddr Nothing (args !! 1)] [NameAddr Nothing (args !! 2)] (args !! 3) body
	print msg

	sendSimpleMessages putStr (head args) domain [msg]

