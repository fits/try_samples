import geb.Page

class TopPage extends Page {
	static url = 'http://localhost:8081/'

	static content = {
		itemLinkList { $('div#data-list a') }
	}
}
