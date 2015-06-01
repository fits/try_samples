@Grab('com.google.api-client:google-api-client-java6:1.20.0')
@Grab("org.apache.httpcomponents:httpclient:4.4.1")
import com.google.api.client.googleapis.auth.oauth2.*
import com.google.api.client.googleapis.GoogleUtils
import com.google.api.client.googleapis.util.Utils

import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.ssl.SSLContextBuilder

def SPREADSHEETS_FEED_BASE = 'https://spreadsheets.google.com/feeds/'
def SPREADSHEETS_FEED = SPREADSHEETS_FEED_BASE + 'spreadsheets/private/full'

def confFile = args.length > 0? args[0]: 'setting.properties'

def conf = new Properties()
conf.load(new File(confFile).newInputStream())

def credential = new GoogleCredential.Builder()
	.setTransport(Utils.getDefaultTransport())
	.setJsonFactory(Utils.getDefaultJsonFactory())
	.setServiceAccountId(conf.mailAddress)
	.setServiceAccountPrivateKeyFromP12File(new File(conf.p12File))
	.setServiceAccountScopes([SPREADSHEETS_FEED_BASE])
	.build()

if (credential.accessToken == null) {
	credential.refreshToken()
}

def token = credential.accessToken

def sslContext = SSLContextBuilder.create().loadKeyMaterial(
	GoogleUtils.getCertificateTrustStore(),
	'notasecret'.toCharArray()
).build()

def sslFactory = new SSLConnectionSocketFactory(sslContext)

def client = HttpClientBuilder.create()
	.setSSLSocketFactory(sslFactory)
	.build()

def get = new HttpGet(SPREADSHEETS_FEED)
get.addHeader('Authorization', "Bearer ${token}")

def res = client.execute(get)

res.entity.writeTo(System.out)
