@Grab('com.google.apis:google-api-services-gmail:v1-rev31-1.20.0')
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.util.Utils
import com.google.api.services.gmail.Gmail

import groovy.json.JsonSlurper

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))

def credential = new GoogleCredential.Builder()
	.setTransport(Utils.getDefaultTransport())
	.setJsonFactory(Utils.getDefaultJsonFactory())
	.setClientSecrets(conf.client_id, conf.client_secret)
	.build()
	.setRefreshToken(token.refresh_token)

def gmail = new Gmail.Builder(
	Utils.getDefaultTransport(), 
	Utils.getDefaultJsonFactory(), 
	credential
).setApplicationName('sample').build()

gmail.users().messages().list('me').setMaxResults(3).execute().messages.each {
	def msg = gmail.users().messages().get('me', it.id).setFormat('minimal').execute()
	println msg
}
