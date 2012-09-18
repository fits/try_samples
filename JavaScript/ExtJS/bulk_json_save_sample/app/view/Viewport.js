
Ext.define('Sample.view.Viewport', {
	extend: 'Ext.container.Viewport',

	layout: 'border',

	requires: [
		'Sample.view.SampleTree'
	],

	items: [
		{
			region: 'center',
			xtype: 'sampletree'
		}
	],

	init: function() {
	}

});