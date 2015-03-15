@Grab('org.gebish:geb-core:0.10.0')
@Grab('com.github.detro:phantomjsdriver:1.2.0')
import geb.Browser

Browser.drive {
	to TopPage

	println title

	waitFor {
		itemLinkList.isDisplayed()
	}

	itemLinkList.head().click()

	at ItemPage

	println title

	quit()
}
