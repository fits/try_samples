@Grab('com.mashape.unirest:unirest-java:1.4.7')
import com.mashape.unirest.http.Unirest

import groovy.json.JsonSlurper
import groovy.json.JsonBuilder
import groovy.transform.BaseScript

@BaseScript TokenScript baseScript

def ticketBuilder = new JsonBuilder()

ticketBuilder (
	version: '1.0',
	print: {}
)

def json = new JsonSlurper()

def conf = json.parse(new File(args[0])).installed
def token = json.parse(new File(args[1]))
def file = new File(args[2])

def newToken = accessToken(
	conf.client_id,
	conf.client_secret,
	token.refresh_token
)

def ticket = ticketBuilder.toString()

def res = Unirest.post('https://www.google.com/cloudprint/submit')
	.header('Authorization', "${newToken.token_type} ${newToken.access_token}")
	.field('printerid', '__google__docs')
	.field('title', file.name)
	.field('ticket', ticket)
	.field('content', file)
	.asJson()

println res.body
