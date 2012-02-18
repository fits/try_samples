
Ext.define('Fits.view.SampleWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.samplewindow',

	title: 'Add Data',
	width: 300,
	height: 200,

	items: [
		{xtype: 'textfield', name: 'title', fieldLabel: 'Title'},
		{xtype: 'numberfield', name: 'priority', fieldLabel: 'Priority'},
		{xtype: 'datefield', name: 'date', fieldLabel: 'Date'}
	],
	buttons: [
		{text: 'Save'},
		{text: 'Cancel'}
	]

});