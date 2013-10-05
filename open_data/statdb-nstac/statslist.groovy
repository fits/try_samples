@Grab("org.apache.httpcomponents:httpclient:4.3")
import org.apache.http.client.methods.*
import org.apache.http.impl.client.*

/**
 * 次世代統計利用システム API を使って統計表情報を取得するスクリプト
 */

def enc = 'UTF-8'

def appId = args[0]
def statsCode = args[1]
def destFileName = (args.length > 2)? args[2]: "statslist_${statsCode}.xml"

def url = "http://statdb.nstac.go.jp/api/1.0b/app/getStatsList?appId=${appId}&statsCode=${statsCode}"

def httpClient = new DefaultHttpClient()
def res = httpClient.execute(new HttpGet(url))

new File(destFileName).withWriter(enc) {
	it.write res.entity.content.getText(enc)
}
