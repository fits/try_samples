
chrome.browserAction.onClicked.addListener(function(tab) {
	var oldAction = '';

	var listener = function(id, inf) {
		if (tab.id == id && inf.status == 'complete') {
			var params = {};

			PARAMETER_KEYS.forEach(function(key) {
				params[key] = localStorage.getItem(key);
			});

			chrome.tabs.sendMessage(tab.id, params, function(res) {
				// 処理が完了するか、同じ処理ページが 2度表示されれば停止
				if (res == 'end' || res == oldAction) {
					chrome.tabs.onUpdated.removeListener(listener);
				}
				oldAction = res;
			});
		}
	};

	chrome.tabs.onUpdated.addListener(listener);

	// callback 関数内で sendMessage を使用すると
	// ページ読み込み完了前にスクリプトが実行されるので都合が悪い
	chrome.tabs.update(tab.id, { url: LOGIN_URL });
});
