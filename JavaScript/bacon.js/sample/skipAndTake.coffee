Bacon = require('baconjs').Bacon

stream = Bacon.fromBinder (sink) ->
	[1..6].forEach (i) -> sink "sample#{i}"

stream.skip(2).take(3).log()
