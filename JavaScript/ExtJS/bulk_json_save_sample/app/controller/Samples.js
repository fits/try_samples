
Ext.define('Sample.controller.Samples', {
	extend: 'Ext.app.Controller',
	models: [
		'Data'
	],
	stores: [
		'Samples'
	],
	views: [
		'SampleTree'
	],

	refs: [
	],

	init: function() {
		this.control({
			'sampletree button[action = update]': {
				click: this.clickUpdate
			},
			'sampletree button[action = save]': {
				click: this.save
			}
		});
	},

	clickUpdate: function() {
		var st = this.getSamplesStore();
		var a = st.getRootNode().findChild('id', 'a');

		var data1 = this.getDataModel().create({
			title: 'aaaa'
		});

		a.appendChild(data1);

		var data2 = this.getDataModel().create({
			title: 'bbbb'
		});

		a.appendChild(data2);

		var id1a = a.findChild('id', 'id:1a');
		id1a.set('title', 'テストデータ');

		var id2a = a.findChild('id', 'id:2a');
		a.removeChild(id2a);
	},
	save: function() {
		var me = this;

		var st = this.getSamplesStore();

		st.save({
			success: function(opt) {
				console.log("success");
			},
			failure: function(opt) {
				console.error(opt);
			}
		});
	}

});