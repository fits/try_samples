
function findActiveElement(doc) {
	var result = doc.activeElement;
	return (result.contentDocument)? findActiveElement(result.contentDocument): result;
}

chrome.extension.onRequest.addListener(function(req, sender, res) {
	var el = findActiveElement(document);
	var result = {};

	if (el) {
		if (req.result) {
			console.log(req.result);
		}
		else {
			result.words = (el.value)? el.value: el.textContent;
		}
	}

	res(result);
});
