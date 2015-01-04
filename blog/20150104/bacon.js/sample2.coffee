Bacon = require('baconjs').Bacon

bus = new Bacon.Bus()

bus.skip(2).take(3).map((s) -> "##{s}").log()

[1..6].forEach (i) -> bus.push "sample#{i}"

bus.end()
