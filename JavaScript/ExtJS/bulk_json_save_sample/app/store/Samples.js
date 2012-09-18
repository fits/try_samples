
Ext.define('Sample.store.Samples', {
	extend: 'Ext.data.TreeStore',

	model: 'Sample.model.Data',
	saveUrl: 'save',

	root: {
		leaf: false,
		expanded: true,
		children: [
	        {title: 'A1', id: 'a', leaf: false},
	        {title: 'B1', id: 'b', leaf: false}
	    ]
	},

	proxy: {
		type: 'rest',
		batchActions: true,
		url: '',
		reader: {
			type: 'json'
		}
	},

	load: function(options) {
		if (options) {
			this.callParent([options]);
		}
		else {
			var me = this;

			me.getRootNode().eachChild(function(child) {
				me.load({ node: child });
			});
		}
	},

	save: function(options) {
		var toCreate = this.getNewRecords();
		var toUpdate = this.getUpdatedRecords();
		var toDestroy = this.getRemovedRecords();

		toCreate.forEach(function(item) {
			item.set('action', 'create');
		});
		toUpdate.forEach(function(item) {
			item.set('action', 'update');
		});
		toDestroy.forEach(function(item) {
			item.set('action', 'destroy');
		});

		var list = toCreate.concat(toUpdate).concat(toDestroy);

		if (list.length > 0) {
			this.sendData(Ext.apply(options, {
				records: list
			}));
		}

		return this;
	},

	sendData: function(options) {
		var writer = this.proxy.writer;

		options = writer.write({ operation: options });

		/* Writer の type によって結果を設定しているプロパティ名が異なる
		*
		* xml  => xmlData
		* json => jsonData
		*
		*/
		var dataType = writer.type + 'Data';
		var data = options[dataType];

		if (data && writer.type === 'json' && !Ext.isArray(data)) {
			data = [data];
		}

		console.log(data);

		var me = this;

		var params = {
			url: me.saveUrl,
			success: function(res) {
				if (options.success) {
					options.success(res);
				}
				me.load();
			},
			failure: function(res) {
				if (options.failure) {
					options.failure(res);
				}
			}
		};
		params[dataType] = data;

		Ext.Ajax.request(params);
	}
});
