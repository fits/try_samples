package sample

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

// Gitblit の Top URL
TOP_URL = 'https://localhost:8443/'

@Grab('org.seleniumhq.selenium:selenium-java:2.33.0')
import org.openqa.selenium.*

class WebTesterDriver {
	private driver

	WebTesterDriver(driver) {
		this.driver = driver
	}

	def button(String caption) {
		try {
			byXpath("//button[text() = '${caption}']")
		} catch (e) {
			null
		}
	}

	def byName(String name) {
		driver.findElement(By.name(name))
	}

	def byXpath(String exp) {
		driver.findElement(By.xpath(exp))
	}

	def to(String url) {
		driver.get(url)
	}
}

Before() {
	drv = new org.openqa.selenium.firefox.FirefoxDriver()
	tester = new WebTesterDriver(drv)
}

After() {
	drv.quit()
}

Given(~'Topへアクセス') { ->
	tester.to(TOP_URL)
}

When(~/"(.*)" ボタンをクリックする/) { caption ->
	tester.button(caption).click()
}

Then(~'エラー表示') { ->
	assert tester.byXpath("//span[@class = 'feedbackPanelERROR']") != null
}
