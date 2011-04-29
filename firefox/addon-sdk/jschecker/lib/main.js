const widgets = require("widget");

var widget = widgets.Widget({
  id: "jschecker",
  label: "JavaScript checker",
  contentURL: "about:blank",
  content: "--",
  onClick: function() {
	console.log("click");
  }
});

console.log("jschecker is running.");
