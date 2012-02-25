
Ext.define('Fits.store.Books', {
	extend: 'Ext.data.Store',

	model: 'Fits.model.Book',

	proxy: {
		type: 'rest',
		url: 'book'
	},
	
	autoLoad: true
});