
function SampleViewModel() {
	var self = this;
	self.title = 'sample';
	self.selected = ko.observable();
	self.page = ko.observable();

	self.items = ko.observableArray([
		{name: 'sample1'},
		{name: 'sample2'}
	]);

	var changePage = function(params, useHistory) {
		if (params.page) {
			if (params.data) {
				self.selected(params.data.name);
			}
			self.page(params.page);

			if (!useHistory) {
				history.pushState(params, '');
			}
		}
	};

	self.goDetail = function(item) {
		changePage({ page: 'detail', data: item });
	};

	self.goTop = function(useHistory) {
		changePage({ page: 'top' });
	};

	window.onpopstate = function(event) {
		var state = event.state
		console.log(state);

		if (state) {
			changePage(state, true);
		}
	};

	self.goTop();
}

ko.applyBindings(new SampleViewModel());
