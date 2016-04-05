@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

import groovy.json.JsonSlurper

addShutdownHook {
	Unirest.shutdown()
}

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))
def feedUrl = args[2]

def res = Unirest.post('https://www.googleapis.com/oauth2/v3/token')
	.field('client_id', conf.client_id)
	.field('client_secret', conf.client_secret)
	.field('grant_type', 'refresh_token')
	.field('refresh_token', token.refresh_token)
	.asJson()

def newToken = res.body.object

def res2 = Unirest.get(feedUrl)
	.header('Authorization', "${newToken.token_type} ${newToken.access_token}")
	.asJson()

println res2.body
