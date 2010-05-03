
const pservice = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefService).getBranch("");

function init() {
    displayJSEnable();
//    displayJavaEnable();
}

function jsclick() {

    pservice.setBoolPref("javascript.enabled", !pservice.getBoolPref("javascript.enabled", false));

    displayJSEnable();
}

function displayJSEnable() {
    var jsenable = pservice.getBoolPref("javascript.enabled", false);

    var doc = document.getElementById("statusbar-jschecker");

    doc.label = (jsenable)? "js": "--";
}

/*
function javaclick() {

    pservice.setBoolPref("security.enable_java", !pservice.getBoolPref("security.enable_java", false));

    displayJavaEnable();
}

function displayJavaEnable() {
    var javaenable = pservice.getBoolPref("security.enable_java", false);

    var doc = document.getElementById("statusbar-javachecker");

    doc.label = (javaenable)? "java": "----";
}
*/

window.addEventListener("load", init, false);
window.addEventListener("focus", init, false);
