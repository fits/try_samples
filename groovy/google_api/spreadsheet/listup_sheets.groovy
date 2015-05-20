@Grab('com.google.api-client:google-api-client-java6:1.20.0')
import com.google.api.client.googleapis.auth.oauth2.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory

import com.google.gdata.client.spreadsheet.SpreadsheetService
import com.google.gdata.data.spreadsheet.SpreadsheetFeed

def SPREADSHEETS_FEED_BASE = 'https://spreadsheets.google.com/feeds/'
def SPREADSHEETS_FEED = SPREADSHEETS_FEED_BASE + 'spreadsheets/private/full'

def confFile = args.length > 0? args[0]: 'setting.properties'

def conf = new Properties()
conf.load(new File(confFile).newInputStream())

def credential = new GoogleCredential.Builder()
	.setTransport(new NetHttpTransport())
	.setJsonFactory(JacksonFactory.getDefaultInstance())
	.setServiceAccountId(conf.mailAddress)
	.setServiceAccountPrivateKeyFromP12File(new File(conf.p12File))
	.setServiceAccountScopes([SPREADSHEETS_FEED_BASE])
	.build()

if (credential.accessToken == null) {
	credential.refreshToken()
}

def token = credential.accessToken

def service = new SpreadsheetService('sample')
service.authSubToken = token

def feed = service.getFeed(new URL(SPREADSHEETS_FEED), SpreadsheetFeed)

feed.entries.each {
	println "--- ${it.title.getPlainText()} ---"

	it.worksheets.each { ws ->
		println "sheet: ${ws.title.getPlainText()}"
	}
}
