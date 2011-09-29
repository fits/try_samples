import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

if (args.length < 4) {
	println "${new File(System.getProperty('script.name')).name} <url> <basic user> <basic password> <post data>"
	return
}

def url = new URL(args[0])
def user = args[1]
def pass = args[2]
def post_data = args[3]

//Basic認証用の設定（getPasswordAuthentication() をオーバーライド）
Authenticator.default = {
	new PasswordAuthentication(user, pass.toCharArray())
} as Authenticator

//SSL 証明書を無視するための設定（HostnameVerifier インターフェースを実装）
//（注）openConnection する前に設定しておく必要あり
HttpsURLConnection.defaultHostnameVerifier = {
	host, session -> true
} as HostnameVerifier

def con = url.openConnection()
con.doOutput = true
con.requestMethod = "POST"

//SSL 証明書を無視するための設定
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
	it.print post_data
}

//ヘッダー出力
con.headerFields each {
	println it
}

//レスポンス出力
println con.inputStream.text
