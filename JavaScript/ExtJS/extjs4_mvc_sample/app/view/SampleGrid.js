Ext.create('Ext.data.Store', {
	storeId: 'sampleStore',
	fields: ['no', 'title', 'priority', 'date', 'details'],
	data: [
		{no: '1', title: 'テストデータ1', priority: 15, date: '2012/02/18 12:00', details: [
			{name: 'AAAA', value: '1000'},
			{name: 'BBB', value: '3000'}
		]},
		{no: '2', title: 'チェックデータ', priority: 20, date: '2012/02/01 00:00', details: [
			{name: 'あああ', value: '90'}
		]}
	]
});


Ext.define('Fits.view.SampleGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.samplegrid',
	store: Ext.data.StoreManager.lookup('sampleStore'),

	tbar: [
		{xtype: 'button', text: '追加', action: 'add'},
		{xtype: 'tbseparator'},
		{xtype: 'tbspacer', width: 20},
		{xtype: 'label', text: '条件：'},
		{xtype: 'textfield'},
		{xtype: 'button', text: '検索'}
	],

	columns: [
		{header: 'No', dataIndex: 'no', width: 40},
		{header: 'Title', dataIndex: 'title', width: 200},
		{header: 'Priority', dataIndex: 'priority', width: 50},
		{header: 'Date', dataIndex: 'date', width: 120},
		{header: 'Details', flex: 1, renderer: function(value, meta, rec) {
			var tpl = new Ext.XTemplate(
				'<ol>',
				'<tpl for=".">',
					'<li>{name} : {value}</li>',
				'</tpl>',
				'</ol>'
			);

			return tpl.apply(rec.data.details);
		}},
	]

});