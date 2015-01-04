fs = require 'fs'
Bacon = require('baconjs').Bacon

toLines = (v) -> v.toString().split '\n'

bus = new Bacon.Bus()

bus.skip(2).take(3).map((s) -> "##{s}").log()

stream = Bacon.fromNodeCallback (cb) ->
	fs.readFile process.argv[2], cb

stream.flatMap(toLines).onValue (vs) ->
	vs.forEach (v) -> bus.push v
