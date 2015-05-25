@Grab("org.apache.httpcomponents:httpclient:4.4.1")
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContextBuilder

import javax.net.ssl.HostnameVerifier
import groovy.json.JsonSlurper

def json = new JsonSlurper()

def token = json.parse(new File(args[0]))

def sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build()

def sslFactory = new SSLConnectionSocketFactory(sslContext, {hostname, session -> true } as HostnameVerifier)

def client = HttpClientBuilder.create()
	.setSSLSocketFactory(sslFactory)
	.build()

def get = new HttpGet('https://www.googleapis.com/gmail/v1/users/me/messages?maxResults=5')
get.addHeader('Authorization', "${token.token_type} ${token.access_token}")

def res = client.execute(get)

res.entity.writeTo(System.out)
