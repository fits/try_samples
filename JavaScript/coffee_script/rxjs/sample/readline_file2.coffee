rx = require 'rxjs'
fs = require 'fs'

fromFile = (file) ->
	rx.Observable.create (observer) ->
		stream = fs.createReadStream(file, {encoding: 'utf-8'})

		stream.on 'error', (ex) -> observer.onError ex
		stream.on 'close', -> console.log '*** close'

		stream.on 'end', -> observer.onCompleted()

		buf = ''

		stream.on 'data', (data) ->
			list = (buf + data).split(/\n|\r\n/)
			buf = list.pop()

			observer.onNext line for line in list

		-> stream.destroy()

fromFile(process.argv[2]).skip(1).take(2).subscribe (x) -> console.log '#' + x
