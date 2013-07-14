package sample

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

// Gitblit の Top URL
TOP_URL = 'https://localhost:8443/'

import org.openqa.selenium.*

class WebTesterDriver {
	private driver

	WebTesterDriver(driver) {
		this.driver = driver
	}

	def button(String caption) {
		byXpath("//button[text() = '${caption}']")
	}

	def link(String caption) {
		byXpath("//a[contains(., '${caption}')]")
	}

	def isError() {
		byXpath("//span[@class = 'feedbackPanelERROR']") != null
	}

	def byName(String name) {
		element(By.name(name))
	}

	def byXpath(String exp) {
		element(By.xpath(exp))
	}

	def element(by) {
		try {
			driver.findElement(by)
		} catch (e) {
			null
		}
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

Given(~'ログイン済み') { ->
	tester.to(TOP_URL)
	tester.byName('username').sendKeys('admin')
	tester.byName('password').sendKeys('admin')
	tester.button('ログイン').click()
}

When(~/"(.*)" ボタンをクリックする/) { caption ->
	tester.button(caption).click()
}

Then(~'エラー表示') { ->
	assert tester.isError()
}
