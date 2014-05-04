window.addEventListener('load', function() {

	var createData = function(src) {
		var res = {};

		PARAMETER_KEYS.forEach(function(key) {
			res[key] = (src && src[key])? src[key]: '';
		});

		return res;
	};

	var saveSettings = function(target) {
		var data = JSON.stringify(target());
		localStorage.setItem(ST_KEY, data);
	};

	var loadSettings = function(target) {
		var settings = localStorage.getItem(ST_KEY);

		if (settings) {
			try {
				var data = JSON.parse(settings);
				target(data);
			} catch (e) {
				console.error(e);
			}
		}
	};

	var rlogin = function(item) {
		if (item) {
			chrome.tabs.getSelected(null, function(tab) {
				var oldAction = '';

				var listener = function(id, inf) {
					if (tab.id == id && inf.status == 'complete') {
						var params = item;

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
		}
	};

	var trim = function(str) {
		return (str)? str.replace(/(^\s+)|(\s+$)/g, ''): str;
	};

	var viewModel = {
		title: ko.observable(),
		items: ko.observableArray(),
		selectedItem: ko.observable(),
		addItem: function() {
			var title = trim(viewModel.title());

			if (title) {
				viewModel.items.push(createData({ title: title }));
				viewModel.title('');
				saveSettings(viewModel.items);
			}
		},
		updateItem: function() {
			var item = viewModel.selectedItem();

			if (item) {
				saveSettings(viewModel.items);
			}
		},
		removeItem: function() {
			var item = viewModel.selectedItem();

			if (item) {
				viewModel.items.remove(item);
				saveSettings(viewModel.items);
			}
		},
		doLogin: function() {
			rlogin(viewModel.selectedItem());
		}
	};

	loadSettings(viewModel.items);

	ko.applyBindings(viewModel);
});
