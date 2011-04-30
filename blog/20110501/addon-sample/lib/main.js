const {Cc, Ci} = require("chrome");
const widgets = require("widget");

var pservice = Cc["@mozilla.org/preferences-service;1"].getService(Ci.nsIPrefService).getBranch("");

//JavaScript の有効設定の有無を表示
var displayJSStatus = function(w) {
  var jsenable = pservice.getBoolPref("javascript.enabled", false);
  w.content = (jsenable)? "JS": "--";
}

var widget = widgets.Widget({
  id: "addon-sample",
  label: "addon-sample",
  content: "--",
  onClick: function() {
	pservice.setBoolPref("javascript.enabled", !pservice.getBoolPref("javascript.enabled", false));

	displayJSStatus(this);
  }
});

//初期表示
displayJSStatus(widget);

