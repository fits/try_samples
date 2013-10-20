
jQuery(function() {
	var viewModel = {
		items: ko.observableArray()
	};

	jQuery.ajax({
		type: 'GET',
		url: 'item',
		success: function(data) {
			viewModel.items(data);
		},
		error: function(msg) {
			console.log(msg);
		}
	});

	ko.applyBindings(viewModel);
});

