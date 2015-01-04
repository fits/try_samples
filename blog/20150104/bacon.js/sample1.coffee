Bacon = require('baconjs').Bacon

bus = new Bacon.Bus()

bus.log()

[1..6].forEach (i) -> bus.push "sample#{i}"

bus.end()
