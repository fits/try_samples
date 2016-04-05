@Grab('com.mashape.unirest:unirest-java:1.4.9')
import com.mashape.unirest.http.Unirest

import groovy.json.JsonSlurper
import groovy.transform.BaseScript

@BaseScript TokenScript baseScript

addShutdownHook {
	Unirest.shutdown()
}

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))

def newToken = accessToken(
	conf.client_id,
	conf.client_secret,
	token.refresh_token
)

def printers = Unirest.get('https://www.google.com/cloudprint/search')
	.header('Authorization', "${newToken.token_type} ${newToken.access_token}")
	.asJson()

println printers.body
