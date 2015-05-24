@Grab('org.gebish:geb-core:0.10.0')
@Grab('com.codeborne:phantomjsdriver:1.2.1')
//@Grab('org.seleniumhq.selenium:selenium-firefox-driver:2.45.0')
@Grab("org.apache.httpcomponents:httpclient:4.4.1")
import geb.Browser
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.ssl.SSLContextBuilder

import javax.net.ssl.HostnameVerifier
import groovy.json.JsonSlurper

def scope = 'https://mail.google.com/%20https://www.googleapis.com/auth/gmail.compose%20https://www.googleapis.com/auth/gmail.modify'

def json = new JsonSlurper()
def conf = json.parse(new File(args[0])).installed

def userId = args[1]
def password = args[2]

def code = null

Browser.drive {
	setDriver(new PhantomJSDriver(new DesiredCapabilities()))

	def url = "${conf.auth_uri}?redirect_uri=${conf.redirect_uris[0]}&response_type=code&client_id=${conf.client_id}&scope=${scope}"

	go url

	$('input[name="Email"]').value(userId)
	$('input[name="Passwd"]').value(password)
	$('input[type="submit"]').click()

	waitFor(30) { $('button[id="submit_approve_access"]').isDisabled() == false }

	$('button[id="submit_approve_access"]').click()

	def codeInput = waitFor(30) { $('input[id="code"]') }

	code = codeInput.value()

	quit()
}

def param = { name, value -> new BasicNameValuePair(name, value) }

def sslContext = SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build()

def sslFactory = new SSLConnectionSocketFactory(sslContext, {hostname, session -> true } as HostnameVerifier)

def client = HttpClientBuilder.create()
	.setSSLSocketFactory(sslFactory)
	.build()

def post = new HttpPost('https://www.googleapis.com/oauth2/v3/token')

post.entity = new UrlEncodedFormEntity([
	param('code', code),
	param('client_id', conf.client_id),
	param('client_secret', conf.client_secret),
	param('grant_type', 'authorization_code'),
	param('redirect_uri', conf.redirect_uris[0])
])

def res = client.execute(post)

def resJson = json.parse(res.entity.content)

def accessToken = resJson.access_token

println accessToken
println resJson
