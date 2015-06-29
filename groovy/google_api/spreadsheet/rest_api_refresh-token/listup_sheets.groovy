@Grab("org.apache.httpcomponents:httpclient:4.5")
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

import groovy.json.JsonSlurper

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))

def param = { name, value -> new BasicNameValuePair(name, value) }

def client = HttpClientBuilder.create().build()

def post = new HttpPost('https://www.googleapis.com/oauth2/v3/token')

post.entity = new UrlEncodedFormEntity([
	param('client_id', conf.client_id),
	param('client_secret', conf.client_secret),
	param('grant_type', 'refresh_token'),
	param('refresh_token', token.refresh_token)
])

def res = client.execute(post)

def newToken = json.parse(res.entity.content)

def get = new HttpGet('https://spreadsheets.google.com/feeds/spreadsheets/private/full')
get.addHeader('Authorization', "${newToken.token_type} ${newToken.access_token}")

client.execute(get).entity.writeTo(System.out)
