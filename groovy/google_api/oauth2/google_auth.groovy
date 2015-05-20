@Grab('com.google.api-client:google-api-client-java6:1.20.0')
import com.google.api.client.googleapis.auth.oauth2.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

def confFile = args.length > 0? args[0]: 'setting.properties'

def conf = new Properties()
conf.load(new File(confFile).newInputStream())

def credential = new GoogleCredential.Builder()
	.setTransport(new NetHttpTransport())
	.setJsonFactory(JacksonFactory.getDefaultInstance())
	.setServiceAccountId(conf.mailAddress)
	.setServiceAccountPrivateKeyFromP12File(new File(conf.p12File))
	.setServiceAccountScopes(['http://spreadsheets.google.com/feeds/'])
	.build()

println credential

if (credential.accessToken == null) {
	credential.refreshToken()
}

println "token : ${credential.accessToken}"
