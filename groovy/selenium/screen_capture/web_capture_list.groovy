@Grab('org.seleniumhq.selenium:selenium-java:2.43.1')
import org.openqa.selenium.*
import org.openqa.selenium.firefox.*

if (args.length < 1) {
	println '<url list file> [<output file prefix>]'
	return
}

def outputPrefix = (args.length > 1)? args[1]: ""

def capture = { drv, url, destFile ->
	drv.get url
	new File(destFile).bytes = drv.getScreenshotAs(OutputType.BYTES)
}

def driver = new FirefoxDriver()

def count = 1
def urlList = new File(args[0])

urlList.readLines().findAll { it.trim().size() > 0 }.each {
	capture driver, it, "${outputPrefix}${count++}.png"
}

driver.close()
