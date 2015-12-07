
rx = require 'rx'

f1 = (cb) -> cb('sample')

st = rx.Observable.fromCallback f1

st().subscribe(
	(x) -> console.log "callback: #{x}",
	(err) -> console.error err
)

