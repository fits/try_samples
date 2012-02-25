
Ext.define('Fits.controller.Books', {
	extend: 'Ext.app.Controller',

	stores: ['Books'],

	init: function() {
		console.log('init samples');

		this.control({
			'bookgrid button[action=add]': {
				click: this.addBook
			}
		});
	},

	addBook: function() {
		console.log("*** add book");

		var data = Ext.create('Fits.model.Book', {
			title: '追加データ'
		});
		data.comments().add({content: 'コメント1'});
		data.comments().add({content: 'コメント2'});
		data.comments().add({content: 'コメント3'});

		var store = this.getBookStore();
		store.add(data);

		store.sync();
	}
});