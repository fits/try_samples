@Grab('org.gebish:geb-core:0.10.0')
@Grab('org.seleniumhq.selenium:selenium-firefox-driver:2.45.0')
import geb.Browser

Browser.drive {
	go 'http://localhost/sample'

	println title

	def alist = $('div.sample a')

	assert alist.size() > 0

	alist.head().click()

	println title

	quit()
}
