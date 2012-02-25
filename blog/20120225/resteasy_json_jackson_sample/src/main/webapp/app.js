
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
		Ext.data.writer.Json.override({
			// getRecordData をオーバーライド
			getRecordData: function() {
				//オーバーライド元のメソッド呼び出し
				var data = this.callOverridden(arguments);

				var record = arguments[0];

				if (record.associations.length > 0) {
					Ext.Array.each(record.associations.keys, function(key) {
						data[key] = [];

						//関連毎の Store 取得
						var assocStore = record[key]();

						Ext.Array.each(assocStore.data.items, function(assocItem) {
							data[key].push(assocItem.data);
						});
					});
				}
				return data;
			}
		});
	}
});