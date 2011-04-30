
const {Cc, Ci} = require("chrome");
const widgets = require("widget");

var pservice = Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefService).getBranch("");

var displayJSStatus = function(w) {
  var jsenable = pservice.getBoolPref("javascript.enabled", false);
  w.content = (jsenable)? "js": "--";
}

var widget = widgets.Widget({
  id: "jschecker",
  label: "JavaScript checker",
  content: "--",
  onClick: function() {
	pservice.setBoolPref("javascript.enabled", !pservice.getBoolPref("javascript.enabled", false));

	displayJSStatus(this);
  }
});

//初期表示
displayJSStatus(widget);

