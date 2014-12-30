fs = require 'fs'
Bacon = require('baconjs').Bacon

toLines = (v) -> v.toString().split '\n'

stream = Bacon.fromNodeCallback (cb) ->
	fs.readFile process.argv[2], cb

stream.flatMap(toLines).onValue (vs) -> 
	Bacon.sequentially(0, vs).skip(1).take(2).map( (v) -> "##{v}" ).log()

