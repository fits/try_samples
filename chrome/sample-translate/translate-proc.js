
var findElement = function(element) {
	if (element.localName.indexOf('frame') > -1) {
		for (var i = 0; i < window.frames.length; i++) {
			if (window.frames[i].window == element.contentWindow) {
				element = window.frames[i].document.activeElement;
				break;
			}
		}
	}
	return (element.localName == 'textarea')? element: null;
};

chrome.extension.onRequest.addListener(function(req, sender, res) {
	var el = findElement(document.activeElement);
	var result = {};

	if (el) {
		if (req.result) {
			console.log(req.result);
			el.value += '\n' + req.result + '\n';
		}
		else {
			result.words = el.value;
		}
	}
	else {
		console.log('not textarea');
	}
	res(result);
});
