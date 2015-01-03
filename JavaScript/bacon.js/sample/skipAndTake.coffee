Bacon = require('baconjs').Bacon

stream = Bacon.fromBinder (sink) ->
	[1..6].forEach (i) -> sink "sample#{i}"

stream.skip(2).take(3).map((s) -> "##{s}").log()

console.log '-----'

stream2 = Bacon.fromArray([1..6].map (i) -> "sample#{i}")

stream2.skip(2).take(3).map((s) -> "##{s}").log()
