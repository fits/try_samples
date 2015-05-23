@Grab('org.gebish:geb-core:0.10.0')
@Grab('com.codeborne:phantomjsdriver:1.2.1')
//@Grab('org.seleniumhq.selenium:selenium-firefox-driver:2.45.0')
import geb.Browser
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

import groovy.json.JsonSlurper

def json = new JsonSlurper()
def conf = json.parse(new File(args[0])).installed

def userId = args[1]
def password = args[2]

Browser.drive {
	setDriver(new PhantomJSDriver(new DesiredCapabilities()))

	def scope = 'https://mail.google.com/%20https://www.googleapis.com/auth/gmail.compose%20https://www.googleapis.com/auth/gmail.modify'

	def url = "${conf.auth_uri}?redirect_uri=${conf.redirect_uris[0]}&response_type=code&client_id=${conf.client_id}&scope=${scope}"

	go url

	$('input[name="Email"]').value(userId)
	$('input[name="Passwd"]').value(password)
	$('input[type="submit"]').click()

	waitFor(30) { $('button[id="submit_approve_access"]').isDisabled() == false }

	$('button[id="submit_approve_access"]').click()

	def code = waitFor(30) { $('input[id="code"]') }

	println code.value()

	quit()
}
