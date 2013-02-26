
$(function () {
	// サンプルデータ
	var data = [
		{
			title: 'サンプル1',
			link: '/sample1.html',
			imageUrl: 'img/sample1.png',
			subImageUrl: 'img/sample_thumb1.png',
			note: 'サンプルの画像データ'
		},
		{
			title: 'サンプル2',
			link: '/sample2.html',
			imageUrl: 'img/sample2.png',
			subImageUrl: 'img/sample_thumb2.png',
			note: 'サンプル2の画像データ'
		},
		{
			title: 'サンプル3',
			link: '/sample3.html',
			imageUrl: 'img/sample3.png',
			subImageUrl: 'img/sample_thumb3.png',
			note: 'サンプル3の画像データ'
		}
	];

	var viewModel = {
		items: ko.observableArray(),
		currentItem: ko.observable(),
		selectItem: function(item) {
			var self = viewModel;
			self.currentItem(item);
		}
	};

	viewModel.items(data);
	viewModel.currentItem(data[0]);

	ko.applyBindings(viewModel);
});