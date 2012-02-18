
Ext.define('Fits.view.TabContent', {
	extend: 'Ext.tab.Panel',
	alias: 'widget.tabcontent',

	items: [
		{
			title: 'タブ1',
			xtype: 'samplegrid'
		},
		{
			title: 'タブ2',
			html: 'none'
		}
	]
});