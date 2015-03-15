@Grab('org.gebish:geb-core:0.10.0')
@Grab('com.github.detro:phantomjsdriver:1.2.0')
import geb.Browser

import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

Browser.drive {
	setDriver(new PhantomJSDriver(new DesiredCapabilities()))

	go 'http://localhost/sample'

	println title

	def alist = $('div.sample a')

	assert alist.size() > 0

	alist.head().click()

	println title

	quit()
}
