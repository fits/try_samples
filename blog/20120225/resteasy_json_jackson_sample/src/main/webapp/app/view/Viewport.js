
Ext.define('Fits.view.Viewport', {
	extend: 'Ext.container.Viewport',

	layout: 'border',

	requires: [
		'Fits.view.BookGrid'
	],

	items: [
		{
			region: 'center',
			xtype: 'bookgrid'
		}
	]

});