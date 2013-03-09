rx = require 'rxjs'
fs = require 'fs'

fromFile = (file) ->
	rx.Observable.create (observer) ->
		fs.readFile file, (err, data) ->
			if err
				observer.onError err
			else
				data.toString().split('\n').forEach (line) ->
					observer.onNext line
				observer.onCompleted()

		->

fromFile(process.argv[2]).skip(1).take(2).subscribe (x) -> console.log '#' + x
