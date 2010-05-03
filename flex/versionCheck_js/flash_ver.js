
function getFlashVersion() {
	var result;

	if (navigator.plugins && navigator.plugins["Shockwave Flash"]) {
		result = getFlashVersionFromGeneral();
	}
	else {
		result = getFlashVersionFromIE();
	}

	return result;
}

function getFlashVersionFromGeneral() {
	var result;

	try {
		var flash = navigator.plugins["Shockwave Flash"];
		result = parseFlashVersionForGeneral(flash.description);
	}
	catch (e) {
	}

	return result;
}

function getFlashVersionFromIE() {
	var version;
	var flash;

	try {
		flash = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
		version = flash.GetVariable("$version");
	}
	catch (e) {
	}

	if (!version) {
		try {
			flash = new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
			version = "WIN 6,0,21,0";

			//6.0.22 - 29 ÇÃ GetVariable Ç≈ÉNÉâÉbÉVÉÖÇ∑ÇÈâÒîçÙ
			axo.AllowScriptAccess = "always";

			version = flash.GetVariable("$version");
		}
		catch (e) {
		}
	}

	return parseFlashVersionForIE(version);
}

function parseFlashVersionForGeneral(versionString) {
	var result;

	if (versionString) {
		var tempString = versionString.replace(/^[A-Za-z\s]+/, "").replace(/(\s+r|\s+b[0-9]+)/, ".");
		var tempArray = tempString.split(".");

		result = {majorVersion: parseInt(tempArray[0]), minorVersion: parseInt(tempArray[1]), revision: parseInt(tempArray[2])};
	}

	return result;
}

function parseFlashVersionForIE(versionString) {
	var result;

	if (versionString) {
		var tempString = versionString.split(" ")[1];
		var tempArray = tempString.split(",");

		result = {majorVersion: tempArray[0], minorVersion: tempArray[1], revision: tempArray[2]};
	}

	return result;
}
