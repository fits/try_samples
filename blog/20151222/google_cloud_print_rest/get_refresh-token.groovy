@Grab('com.mashape.unirest:unirest-java:1.4.7')
import com.mashape.unirest.http.Unirest

import groovy.json.JsonSlurper

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def code = args[1]

def res = Unirest.post('https://www.googleapis.com/oauth2/v3/token')
	.field('code', code)
	.field('client_id', conf.client_id)
	.field('client_secret', conf.client_secret)
	.field('grant_type', 'authorization_code')
	.field('redirect_uri', conf.redirect_uris[0])
	.asJson()

println res.body
