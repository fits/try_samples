
Ext.define('Sample.model.Data', {
	extend: 'Ext.data.Model',

	fields: [
	    {name: 'id', type: 'string'},
		{name: 'title', type: 'string'}, 
		{name: 'action', type: 'string'},
		{name: 'leaf', defaultValue: true}
	]

});