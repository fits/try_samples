
Ext.define('Fits.model.Book', {
	extend: 'Ext.data.Model',

	fields: [
		{name: 'id', type: 'string'},
		{name: 'title', type: 'string'}
	],

	hasMany: [
		{model: 'Fits.model.Comment', name: 'comments'}
	]

});