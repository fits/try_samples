var Settings = function() {
};

Settings.prototype = {
	prefix: 'sampletranslate.',

	get: function(key) {
		return localStorage[this.prefix + key];
	},
	set: function(key, value) {
		localStorage[this.prefix + key] = value;
	}
};
