@Grab("org.apache.httpcomponents:httpclient:4.4.1")
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.ssl.SSLContextBuilder

import javax.net.ssl.HostnameVerifier
import groovy.json.JsonSlurper

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))

def param = { name, value -> new BasicNameValuePair(name, value) }

def sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build()

def sslFactory = new SSLConnectionSocketFactory(sslContext, {hostname, session -> true } as HostnameVerifier)

def client = HttpClientBuilder.create()
	.setSSLSocketFactory(sslFactory)
	.build()

def post = new HttpPost('https://www.googleapis.com/oauth2/v3/token')

post.entity = new UrlEncodedFormEntity([
	param('client_id', conf.client_id),
	param('client_secret', conf.client_secret),
	param('grant_type', 'refresh_token'),
	param('refresh_token', token.refresh_token)
])

def res = client.execute(post)

def resJson = json.parse(res.entity.content)

def accessToken = resJson.access_token

println accessToken
println resJson
