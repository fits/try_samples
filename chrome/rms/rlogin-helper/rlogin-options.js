window.addEventListener('load', function() {
	var viewModel = {};

	['rloginId', 'rloginPassword', 'userId', 'userPassword'].forEach(function(key) {
		var value = localStorage.getItem(key);

		if (!value) {
			value = '';
		}

		viewModel[key] = ko.observable(value);

		viewModel[key].subscribe(function(newValue) {
			localStorage.setItem(key, newValue);
		});
	});

	ko.applyBindings(viewModel);
});
