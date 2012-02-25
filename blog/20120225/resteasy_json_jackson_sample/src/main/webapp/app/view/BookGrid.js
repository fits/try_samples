
Ext.define('Fits.view.BookGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.bookgrid',
	store: 'Books',

	tbar: [
		{xtype: 'button', text: 'Add', action: 'add'},
		{xtype: 'button', text: 'Refresh', action: 'refresh'},
		{xtype: 'tbseparator'}
	],

	columns: [
		{header: 'Id', dataIndex: 'id', width: 40},
		{header: 'Title', dataIndex: 'title', width: 200},
		{header: 'Comments', flex: 1, renderer: function(value, meta, rec) {
			var tpl = new Ext.XTemplate(
				'<ol>',
				'<tpl for="items">',
					'<li>{data.content}</li>',
				'</tpl>',
				'</ol>'
			);

			return tpl.apply(rec.comments().data);
		}},
	]

});