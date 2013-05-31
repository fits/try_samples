
function findActiveElement(doc) {
	var result = doc.activeElement;

	return (result.contentWindow)? findActiveElement(result.contentWindow.document): result;
}

function getActive() {
	return findActiveElement(document);
}
