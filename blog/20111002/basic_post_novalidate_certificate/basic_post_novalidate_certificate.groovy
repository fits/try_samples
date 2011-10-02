import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

def url = new URL(args[0])
def user = args[1]
def pass = args[2]
def postData = args[3]

//Basic認証（getPasswordAuthentication() をオーバーライド）
Authenticator.default = {
	new PasswordAuthentication(user, pass.toCharArray())
} as Authenticator

// ホスト名を検証しないようにする設定
//（注）openConnection する前に設定しておく必要あり
HttpsURLConnection.defaultHostnameVerifier = {
	host, session -> true
} as HostnameVerifier

def con = url.openConnection()
con.doOutput = true
con.requestMethod = "POST"

//SSL 証明書を検証しないための設定
def sslctx = SSLContext.getInstance("SSL")
def tmanager = [
	checkClientTrusted: {chain, authType -> },
	checkServerTrusted: {chain, authType -> },
	getAcceptedIssuers: {null}
] as X509TrustManager

sslctx.init(null as KeyManager[], [tmanager] as X509TrustManager[], new SecureRandom())

con.SSLSocketFactory = sslctx.socketFactory

//POSTデータの出力
con.outputStream.withWriter {
	it.print postData
}

//結果の出力
print con.inputStream.text
