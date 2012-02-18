
Ext.define('Fits.view.Viewport', {
	extend: 'Ext.container.Viewport',

	layout: 'border',

	requires: [
		'Fits.view.TabContent',
		'Fits.view.SearchTool'
	],

	items: [
		{
			region: 'center',
			xtype: 'tabcontent'
		},
		{
			region: 'north',
			xtype: 'searchtool'
		}
	],

	init: function() {
		console.log('init page');
	}

});