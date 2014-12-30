Bacon = require('baconjs').Bacon

append = (a, b) -> a + b

Bacon.sequentially(1000, ['a1', 'b2', 'c3']).scan('-', append).log()
