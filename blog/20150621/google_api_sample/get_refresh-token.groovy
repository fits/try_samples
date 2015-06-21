@Grab("org.apache.httpcomponents:httpclient:4.5")
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair

import groovy.json.JsonSlurper

def json = new JsonSlurper()
def conf = json.parse(new File(args[0])).installed

def code = args[1]

def param = { name, value -> new BasicNameValuePair(name, value) }

def client = HttpClientBuilder.create().build()

def post = new HttpPost('https://www.googleapis.com/oauth2/v3/token')

post.entity = new UrlEncodedFormEntity([
	param('code', code),
	param('client_id', conf.client_id),
	param('client_secret', conf.client_secret),
	param('grant_type', 'authorization_code'),
	param('redirect_uri', conf.redirect_uris[0])
])

def res = client.execute(post)

res.entity.writeTo(System.out)
