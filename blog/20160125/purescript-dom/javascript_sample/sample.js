
var Sample = {
	updateContent: (id, content) => {
		var node = document.getElementById(id);

		if (node) {
			node.textContent = content;
		}
	}
};