
//自作の View や Controller をロードするための設定
Ext.Loader.setConfig({
	enabled: true
});

Ext.application({
	name: 'Fits',
	appFolder: 'app',
	// app/view/Viewport.js を使うための設定
	autoCreateViewport: true,

	controllers: [
		'Books'
	],

	launch: function() {
	}
});