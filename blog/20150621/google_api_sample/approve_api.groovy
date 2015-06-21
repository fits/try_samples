@Grab('org.gebish:geb-core:0.10.0')
@Grab('com.codeborne:phantomjsdriver:1.2.1')
import geb.Browser
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

import groovy.json.JsonSlurper

def json = new JsonSlurper()
def conf = json.parse(new File(args[0])).installed

def userId = args[1]
def password = args[2]
def scope = [
	'https://mail.google.com/',
	'https://www.googleapis.com/auth/gmail.compose',
	'https://www.googleapis.com/auth/gmail.modify'
].join('%20')

def code = null

Browser.drive {
	setDriver(new PhantomJSDriver(new DesiredCapabilities()))

	def url = "${conf.auth_uri}?redirect_uri=${conf.redirect_uris[0]}&response_type=code&client_id=${conf.client_id}&scope=${scope}"

	go url

	$('input[name="Email"]').value(userId)
	$('input[type="submit"]').click()

	waitFor(30) { $('div.second div.slide-in').isDisplayed() }

	$('input[name="Passwd"]').value(password)
	$('div.second input[type="submit"]').click()

	waitFor(30) { $('button[id="submit_approve_access"]').isDisabled() == false }

	$('button[id="submit_approve_access"]').click()

	def codeInput = waitFor(30) { $('input[id="code"]') }

	code = codeInput.value()

	quit()
}

println code
