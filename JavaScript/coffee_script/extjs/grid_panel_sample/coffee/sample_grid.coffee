
Ext.require([
	'Ext.data.*'
	'Ext.grid.*'
	'Ext.tree.*'
])

Ext.onReady(-> 
	ds = Ext.create('Ext.data.ArrayStore', {
		fields: [
			{name: 'title', type: 'string'}
			{name: 'priority', type: 'integer'}
			{name: 'startDate', type: 'date', dateFormat: 'Y-n-j G:i'}
			{name: 'endDate', type: 'date', dateFormat: 'Y-n-j G:i'}
		]
		data: [
			['テスト1', 30, '2012-02-10 13:00', '']
			['テスト2', 20, '', '']
		]
	})

	tree = Ext.create('Ext.grid.GridPanel', 
		title: 'サンプル'
		width: 700
		height: 300
		renderTo: Ext.getBody()
		store: ds
		columns: [
			{text: 'Title', dataIndex: 'title'}
			{text: 'Priority', dataIndex: 'priority'}
			{text: 'StartDate', dataIndex: 'startDate', renderer: Ext.util.Format.dateRenderer('Y-m-d H:i')}
			{text: 'EndDate', dataIndex: 'endDate'}
		]
	)

)
