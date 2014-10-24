@Grab('org.seleniumhq.selenium:selenium-java:2.43.1')
import org.openqa.selenium.*
import org.openqa.selenium.firefox.*

if (args.length < 2) {
	println '<url> <output file>'
	return
}

def driver = new FirefoxDriver()

driver.get args[0]

def buf = driver.getScreenshotAs(OutputType.BYTES)

new File(args[1]).bytes = buf

driver.close()
