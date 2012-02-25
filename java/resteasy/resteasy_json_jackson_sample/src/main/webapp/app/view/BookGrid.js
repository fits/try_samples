/*
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
*/

Ext.define('Fits.view.BookGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.bookgrid',
	store: 'Books',

	tbar: [
		{xtype: 'button', text: 'Add', action: 'add'},
		{xtype: 'tbseparator'}
	],

	columns: [
		{header: 'Id', dataIndex: 'id', width: 40},
		{header: 'Title', dataIndex: 'title', width: 200},
		{header: 'Comments', flex: 1, renderer: function(value, meta, rec) {
			var tpl = new Ext.XTemplate(
				'<ol>',
				'<tpl for=".">',
					'<li>{content}</li>',
				'</tpl>',
				'</ol>'
			);

			return tpl.apply(rec.data.comments);
		}},
	]

});