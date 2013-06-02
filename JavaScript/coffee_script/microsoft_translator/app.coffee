
https = require 'https'
http = require 'http'
url = require 'url'

authUrl = 'https://datamarket.accesscontrol.windows.net/v2/OAuth2-13'
translateUrl = 'http://api.microsofttranslator.com/V2/Ajax.svc/Translate'

clientId = process.argv[2]
secretKey = process.argv[3]
message = process.argv[4]

authOpts = url.parse authUrl
authOpts.method = 'POST'

postData = "grant_type=client_credentials&client_id=#{encodeURIComponent(clientId)}&client_secret=#{encodeURIComponent(secretKey)}&scope=http://api.microsofttranslator.com"

req = https.request authOpts, (res) ->
	res.on 'data', (data) ->
		token = JSON.parse data

		if token.access_token
			http.get("#{translateUrl}?appId=Bearer%20#{encodeURIComponent(token.access_token)}&to=ja&text=#{encodeURIComponent(message)}", (trRes) ->

				trRes.on 'data', (trData) -> console.log trData.toString()

			).on 'error', (e) -> console.error e

req.on 'error', (e) -> console.error e

req.write postData

req.end()


