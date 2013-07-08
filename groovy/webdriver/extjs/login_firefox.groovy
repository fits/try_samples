@Grab('org.seleniumhq.selenium:selenium-java:2.33.0')
import org.openqa.selenium.*
import org.openqa.selenium.firefox.*

def url = 'http://localhost:8080/sample/'

// ExtJS のボタン取得
def button = { drv, caption ->
	try {
		drv.findElement(By.xpath("//button[span[1] = '${caption}']"))
	} catch (e) {
		null
	}
}

def byName = { drv, name -> 
	drv.findElement(By.name(name))
}

def login = { drv, user, pass ->
	def loginBtn = button(drv, 'ログイン')

	byName(drv, 'userId').sendKeys(user)
	byName(drv, 'password').sendKeys(pass)

	loginBtn.click()
}

//def profile = new FirefoxProfile()
//profile.addExtension(new File("firebug-1.9.1-fx.xpi"))

def driver = new FirefoxDriver()

driver.get(url)

login(driver, 'user1', 'pass1')

println "res: " + (button(driver, 'ログイン') == null)

System.in.read()

driver.quit()
