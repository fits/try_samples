@Grab('org.seleniumhq.selenium:selenium-java:2.38.0')
import org.openqa.selenium.*
import org.openqa.selenium.firefox.*
import org.openqa.selenium.support.ui.WebDriverWait

def loginUrl = 'https://glogin.rms.rakuten.co.jp/'
def rmsUrl = 'https://mainmenu.rms.rakuten.co.jp/'

if (args.length < 4) {
	println 'args: <R-Login ID> <R-Login Password> <UserID> <UserPassword>'
	return
}

def rloginId = args[0]
def rloginPass = args[1]
def userId = args[2]
def userPass = args[3]

def byName = { drv, name -> 
	drv.findElement(By.name(name))
}

def byXpath = { drv, xpath ->
	drv.findElement(By.xpath(xpath))
}

def login = { drv, id1, pass1, id2, pass2 ->
	drv.get(loginUrl)

	byName(drv, 'login_id').sendKeys id1
	byName(drv, 'passwd').sendKeys pass1
	byName(drv, 'submit').click()

	byName(drv, 'user_id').sendKeys id2
	byName(drv, 'user_passwd').sendKeys pass2
	byName(drv, 'submit').click()

	byName(drv, 'submit').click()
}

def toRms = { drv ->
	drv.get "${rmsUrl}?act=login&sp_id=1"

	byXpath(drv, '//input[@type="submit"]').click()

	drv.executeScript 'document.getElementById("fancybox-close").click()'
}

def driver = new FirefoxDriver()

login driver, rloginId, rloginPass, userId, userPass

toRms driver
