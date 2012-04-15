var http = require('http');
var url = require('url');
var fs = require('fs');
var path = require('path');

var dir = process.argv[2];

//エラーメッセージの出力
var printError = function(urlString, error) {
	console.log('failed: ' + urlString + ', ' + error.message);
}

process.stdin.resume();

process.stdin.on('data', function(urls) {
	urls.toString().trim().split('\n').forEach(function(u) {

		var trgUrl = url.parse(u);

		//URL 接続
		http.get(trgUrl, function(res) {
			res.setEncoding('binary');
			var buf = '';

			//データダウンロード
			res.on('data', function(chunk) {
				buf += chunk;
			});

			//ダウンロード完了時の処理
			res.on('end', function() {
				var filePath = path.join(dir, path.basename(trgUrl.pathname));

				//ファイル出力
				fs.writeFile(filePath, buf, 'binary', function(err) {
					if (err) {
						printError(u, err);
					}
					else {
						console.log('downloaded: ' + u + ' => ' + filePath);
					}
				});
			});

			//接続後のエラー処理
			res.on('close', function(err) {
				if (err) {
					printError(u, err);
				}
			});

		}).on('error', function(err) {
			printError(u, err);
		});
	});
});
