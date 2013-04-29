
var viewModel = {
	selectedDir: ko.observable(''),
	dirList: ko.observableArray([]),
	imageList: ko.observableArray([]),
	nameCondition: ko.observable(''),
	onlyUndone: ko.observable(false),

	backup: function(image) {
		$.getJSON('backup/' + image.dir + '/' + image.name, function(data) {
			image.done(data.done);
		});
	}
};

viewModel.selectedDir.subscribe(function(dir) {
	if (dir) {
		viewModel.nameCondition('');

		$.getJSON('image/' + dir, function(data) {
			viewModel.imageList(data.map(function(item) {
				item.done = ko.observable(item.done);
				return item;
			}));
		});
	}
	else {
		viewModel.imageList([]);
	}
});

viewModel.showImageList = ko.computed(function() {
	var filter = viewModel.nameCondition().toLowerCase();
	var checkUndone = viewModel.onlyUndone();

	return ko.utils.arrayFilter(viewModel.imageList(), function(image) {
		var doneResult = (checkUndone)? !image.done(): true;

		return (doneResult && image.name.toLowerCase().indexOf(filter) > -1);
	});
});

ko.applyBindings(viewModel);

$.getJSON('list', function(data) {
	data.unshift('');
	viewModel.dirList(data);
});
