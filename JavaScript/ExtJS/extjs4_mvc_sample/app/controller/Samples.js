
Ext.define('Fits.controller.Samples', {
	extend: 'Ext.app.Controller',
	views: ['SampleWindow'],

	// getSampleWindow() で sampleWindow を取得するための設定
	refs: [
		{
			ref: 'sampleWindow', 
			selector: 'samplewindow',
			autoCreate: true, 
			xtype: 'samplewindow'
		}
	],

	init: function() {
		console.log('init samples');

		this.control({
			'samplegrid button[action=add]': {
				click: this.addSample
			}
		});
	},

	addSample: function() {
		console.log("*** add sample");
		// refs 定義で sampleWindow が取得できる
		this.getSampleWindow().show();
	}
});