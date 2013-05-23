
jQuery(document).ready(function() {
	var settings = new Settings();

	['clientId', 'secretKey'].forEach(function(key) {
		var el = jQuery('#' + key);

		el.val(settings.get(key));

		el.change(function() {
			settings.set(key, el.val());
		});
	});
});
