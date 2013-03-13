rx = require 'rxjs'
fs = require 'fs'

fromFile = (file) ->
	rx.Observable.create (observer) ->
		fs.readFile file, (err, data) ->
			if err
				# エラー発生時
				observer.onError err
			else
				data.toString().split(/\n|\r\n/).forEach (line) ->
					# 1行分のデータを PUSH
					observer.onNext line
				# 完了時
				observer.onCompleted()

		# 何もしない関数を返す
		->

fromFile(process.argv[2]).skip(1).take(2).subscribe (x) -> console.log '#' + x
