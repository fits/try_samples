@Grab("org.apache.httpcomponents:httpclient:4.5")
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

import groovy.xml.MarkupBuilder
import groovy.json.JsonSlurper
import groovy.util.XmlSlurper

def jsonParser = new JsonSlurper()
def xmlParser = new XmlSlurper(false, false)

def param = { name, value -> new BasicNameValuePair(name, value) }

def httpClient = HttpClientBuilder.create().build()

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

def cellXml = { key, worksheetId, row, col, value ->
	def sw = new StringWriter()

	new MarkupBuilder(sw).entry(
		xmlns: 'http://www.w3.org/2005/Atom',
		'xmlns:gs': 'http://schemas.google.com/spreadsheets/2006'
	) {
		id("https://spreadsheets.google.com/feeds/cells/${key}/${worksheetId}/private/full/R${row}C${col}")

		link (rel: 'edit', type: 'application/atom+xml', href: "https://spreadsheets.google.com/feeds/cells/${key}/${worksheetId}/private/full/R${row}C${col}")

		'gs:cell' (row: row, col: col, inputValue: value)
	}

	sw.toString()
}

def postCell = { token, key, worksheetId, xmlString ->
	def post = new HttpPost("https://spreadsheets.google.com/feeds/cells/${key}/${worksheetId}/private/full")

	post.addHeader('Authorization', "${token.token_type} ${token.access_token}")
	post.entity = new StringEntity(xmlString, ContentType.create('application/atom+xml', 'UTF-8'))

	def res = httpClient.execute(post)

	println res.entity.content.text
}

def conf = jsonParser.parse(new File(args[0])).installed
def token = jsonParser.parse(new File(args[1]))
def key = args[2]
def worksheetId = args[3]
def row = args[4] as int
def col = args[5] as int
def value = args[6]

def accessToken = getAccessToken(conf.client_id, conf.client_secret, token.refresh_token)

def proc = cellXml.curry(key, worksheetId) >> postCell.curry(accessToken, key, worksheetId)

proc(row, col, value)
