
Ext.define('Fits.view.TabContent', {
	extend: 'Ext.tab.Panel',
	alias: 'widget.tabcontent',

	items: [
		{
			title: 'タブ1',
			html: 'test data'
		},
		{
			title: 'タブ2',
			html: 'none'
		}
	]
});