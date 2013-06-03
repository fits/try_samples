
jQuery(document).ready(function() {
	var authUrl = 'https://datamarket.accesscontrol.windows.net/v2/OAuth2-13';
	var translateUrl = 'http://api.microsofttranslator.com/V2/Ajax.svc/Translate';

	var settings = new Settings();

	var getToken = function(callback) {
		var params = {
			grant_type: 'client_credentials',
			client_id: settings.get('clientId'),
			client_secret: settings.get('secretKey'),
			scope: 'http://api.microsofttranslator.com'
		};

		jQuery.post(authUrl, params, callback);
	};

	var translate = function(to, words, callback) {
		if (to && words) {
			getToken(function(token) {
				var params = [
					'appId=' + encodeURIComponent('Bearer ' + token.access_token),
					'to=' + to,
					'text=' + encodeURIComponent(words)
				];

				var url = translateUrl + '?' + params.join('&');

				jQuery.ajax({
					type: 'GET',
					url: url,
					dataType: 'text',
					success: function(data) {
						callback(data.replace(/"/g, ''));
					},
					error: function(obj, status, er) {
						console.error(er);
					}
				});
			});
		}
	};

	jQuery('#translate').click(function() {
		var tabs = chrome.tabs;

		var to = jQuery('#to').val();

		tabs.getSelected(function(tab) {
			tabs.sendRequest(tab.id, {}, function(res) {
				if (res && res.words) {
					translate(to, res.words, function(data) {
						tabs.sendRequest(tab.id, { result: data }, function(res) {
						});
					});
				}
			});
		});
	});
});
