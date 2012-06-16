
c = require './combine'

f1 = (args, cb) ->
	console.log "f1 : #{args}"
	cb null, [args, "aaa"]

f2 = (args, cb) ->
	console.log "f2 : #{args}"
	cb null, 10

f3 = (args, cb) ->
	console.log "f3 : #{args}"
	cb null, "abc"

fe = (args, cb) ->
	console.log "fe : #{args}"
	cb 'error'

cf1 = c.combine f1, f2, f3

cf1 'test', (err, res) ->
	console.log "result1 : #{err}, #{res}"

console.log '-----'

cf2 = c.combine f1, fe, f2, f3

cf2 'test', (err, res) ->
	console.log "result2 : #{err}, #{res}"

console.log '-----'

cf3 = c.combine f1, f2, f3, fe

cf3 'test', (err, res) ->
	console.log "result3 : #{err}, #{res}"

console.log '-----'

cf4 = c.combine fe, f2

cf4 'test', (err, res) ->
	console.log "result4 : #{err}, #{res}"

console.log '-----'

cf5 = c.combine f2, f1

cf5 'test', (err, res) ->
	console.log "result5 : #{err}, #{res}"
