
function findActiveElement(doc) {
	if (doc) {
		var result = doc.activeElement;
		return (result.contentDocument)? findActiveElement(result.contentDocument): result;
	}
	else {
		return findActiveElement(document);
	}
}
