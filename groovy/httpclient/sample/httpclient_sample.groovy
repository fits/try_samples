@Grab("org.apache.httpcomponents:httpclient:4.2.1")
import org.apache.http.client.methods.*
import org.apache.http.impl.client.*

def req = new HttpGet(args[0])

// ヘッダーを設定するには setHeader や addHeader を使用
// req.setHeader('Host', 'xxx')

def httpClient = new DefaultHttpClient()
def res = httpClient.execute(req)

println res

/* コンテンツの内容を取得するには getContent で InputStream を取得するか
 * writeTo で OutputStream に書き込む
 */
//println res.entity.content.text
/*
new File("dest.html").withOutputStream {
	res.entity.writeTo(it)
}
*/
