
Ext.define('Fits.view.SearchTool', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.searchtool',

	layout: 'hbox',

	height: 30,

	items: [
		{
			xtype: 'label',
			text: 'コード：'
		},
		{
			xtype: 'combobox'
		}
	]
});