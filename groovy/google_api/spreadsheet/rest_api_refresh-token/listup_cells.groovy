@Grab("org.apache.httpcomponents:httpclient:4.5")
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

import groovy.json.JsonSlurper
import groovy.util.XmlSlurper

def jsonParser = new JsonSlurper()
def xmlParser = new XmlSlurper(false, false)

def param = { name, value -> new BasicNameValuePair(name, value) }

def httpClient = HttpClientBuilder.create().build()

def findFeedUrl = { rel, title, xml ->
	xml.entry.find { it.title == title }.link.find { it.@rel == rel }.@href.text()
}

def getAccessToken = { clientId, secret, refreshToken -> 
	def post = new HttpPost('https://www.googleapis.com/oauth2/v3/token')

	post.entity = new UrlEncodedFormEntity([
		param('client_id', clientId),
		param('client_secret', secret),
		param('grant_type', 'refresh_token'),
		param('refresh_token', refreshToken)
	])

	def res = httpClient.execute(post)

	jsonParser.parse(res.entity.content)
}

def getFeed = { token, feedUrl ->
	def get = new HttpGet(feedUrl)
	get.addHeader('Authorization', "${token.token_type} ${token.access_token}")

	def res = httpClient.execute(get)

	xmlParser.parse(res.entity.content)
}

def feedUrl = 'https://spreadsheets.google.com/feeds/spreadsheets/private/full'
def worksheetFeedRel = 'http://schemas.google.com/spreadsheets/2006#worksheetsfeed'
def cellFeedRel = 'http://schemas.google.com/spreadsheets/2006#cellsfeed'

def conf = jsonParser.parse(new File(args[0])).installed
def token = jsonParser.parse(new File(args[1]))
def spreadsheetTitle = args[2]
def sheetTitle = args[3]

def accessToken = getAccessToken(conf.client_id, conf.client_secret, token.refresh_token)

def getFeedFunc = getFeed.curry(accessToken)

def proc = 
	getFeedFunc >> 
	findFeedUrl.curry(worksheetFeedRel, spreadsheetTitle) >>
	getFeedFunc >> 
	findFeedUrl.curry(cellFeedRel, sheetTitle) >>
	getFeedFunc

def res = proc(feedUrl)

res.entry.each {
	println "${it.title.text()} : ${it.content.text()}"
}
