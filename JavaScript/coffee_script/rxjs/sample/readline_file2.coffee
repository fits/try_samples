rx = require 'rxjs'
fs = require 'fs'

fromFile = (file) ->
	rx.Observable.create (observer) ->
		buf = ''

		stream = fs.createReadStream(file, {encoding: 'utf-8'})

		stream.on 'error', (ex) -> observer.onError ex
		stream.on 'close', -> console.log '*** close'

		stream.on 'end', -> observer.onCompleted()

		stream.on 'data', (data) ->
			tempBuf = ''

			for ch, i in buf + data
				if ch is '\n'
					observer.onNext tempBuf
					tempBuf = ''
				else
					tempBuf += ch

			buf = tempBuf

		-> stream.destroy()

fromFile(process.argv[2]).skip(1).take(2).subscribe (x) -> console.log '#' + x
