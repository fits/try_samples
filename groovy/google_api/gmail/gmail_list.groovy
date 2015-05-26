@Grab('com.google.apis:google-api-services-gmail:v1-rev29-1.20.0')
@Grab('javax.mail:javax.mail-api:1.5.3')
import com.google.api.client.googleapis.auth.oauth2.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.Gmail

import groovy.json.JsonSlurper

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))

def httpTrans = new NetHttpTransport()
def jsonFactory = JacksonFactory.getDefaultInstance()

def credential = new GoogleCredential.Builder()
	.setTransport(httpTrans)
	.setJsonFactory(jsonFactory)
	.setClientSecrets(conf.client_id, conf.client_secret)
	.build()
	.setRefreshToken(token.refresh_token)

def gmail = new Gmail.Builder(httpTrans, jsonFactory, credential)
	.setApplicationName('sample')
	.build()

gmail.users().messages().list('me').setMaxResults(5).execute().messages.each { println it }
