@Grab("org.apache.httpcomponents:httpclient:4.3.1")
@Grab("org.jsoup:jsoup:1.7.3")
import org.apache.http.client.ResponseHandler
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup

def loginUrl = 'https://glogin.rms.rakuten.co.jp/'
def rmsUrl = 'https://mainmenu.rms.rakuten.co.jp/'

if (args.length < 4) {
	println 'args: <R-Login ID> <R-Login Password> <UserID> <UserPassword>'
	return
}

def rloginId = args[0]
def rloginPass = args[1]
def userId = args[2]
def userPass = args[3]

// HTML を Jsoup でパース
def parseHtml = { res -> Jsoup.parse(res.entity.content, null, '')}
// レスポンスの消費
def consumeRes = { res -> EntityUtils.consume(res.entity) }

def http = new DefaultHttpClient()

http.metaClass.get = { String url, ResponseHandler handler = consumeRes ->
	execute(new HttpGet(url), handler)
}

http.metaClass.post = { String url, Map params = [], ResponseHandler handler = consumeRes ->
	def req = new HttpPost(loginUrl)

	if (params && params.size() > 0) {
		req.entity = new UrlEncodedFormEntity(params.collect { k, v ->
			new BasicNameValuePair(k, v)
		})
	}
	execute(req, handler)
}

// ----- ログイン開始 -----

// (1) R-Login ID 認証
def doc = http.post(loginUrl, [
	'module': 'BizAuth',
	'action': 'BizAuthCustomerAttest',
	'submit': '%B3%DA%C5%B7%B2%F1%B0%F7%A4%CE%C7%A7%BE%DA%A4%D8',
	'login_id': rloginId,
	'passwd': rloginPass
], parseHtml)

// (2) 楽天会員の認証
def doc2 = http.post(loginUrl, [
	'module': 'BizAuth',
	'action': 'BizAuthUserAttest',
	'submit': doc.select('input[type=submit]').first().attr('value'),
	'biz_login_id': doc.select('input[name=biz_login_id]').first().attr('value'),
	'business_id': doc.select('input[name=business_id]').first().attr('value'),
	'user_id': userId,
	'user_passwd': userPass
], parseHtml)

// (3) お知らせ画面の承認
http.post(loginUrl, [
	'module': 'BizAuth',
	'action': 'BizAuthAnnounce',
	'submit': doc2.select('input[type=submit]').first().attr('value'),
	'__suid': doc2.select('input[name=__suid]').first().attr('value')
])

// (4) 確認画面表示
http.get("${rmsUrl}?act=login&sp_id=1")

// (5) RMS メイン画面表示
def doc3 = http.get(rmsUrl, parseHtml)

// (6) 各種画面へのログイン処理
doc3.select('img[width=1][height=1]').each {
	http.get(it.attr('src'))
}

// ----- ログイン完了 -----

// 商品ページ設定画面の取得
def productUrl = 'https://item.rms.rakuten.co.jp/rms/mall/rsf/item/vc?__event=RI00_001_101'

http.get(productUrl) { res ->
	println res.entity.content.getText('EUC-JP')
}

// ログアウト
http.get("${loginUrl}?module=BizAuth&action=BizAuthLogout&sp_id=1") { res ->
	println "logout: ${res.statusLine.statusCode}"
	consumeRes res
}
