
import com.mashape.unirest.http.Unirest
import groovy.json.JsonSlurper

abstract class TokenScript extends Script {

	def accessToken(String clientId, String clientSecret, String refreshToken) {
		def res = Unirest.post('https://www.googleapis.com/oauth2/v3/token')
			.field('client_id', clientId)
			.field('client_secret', clientSecret)
			.field('grant_type', 'refresh_token')
			.field('refresh_token', refreshToken)
			.asJson()

		res.body.object
	}
}
