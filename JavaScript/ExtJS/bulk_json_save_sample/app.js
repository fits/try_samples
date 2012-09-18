
Ext.Loader.setConfig({
	enabled:true
});

Ext.application({
	name: 'Sample',
	appFolder: 'app',
	autoCreateViewport: true,

	models: [
	],

	controllers: [
		'Samples'
	],

	launch: function() {
	}
});