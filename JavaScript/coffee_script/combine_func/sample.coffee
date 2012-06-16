c = require './compose'

# 関数1
f1 = (args, cb) ->
	console.log "f1 : #{args}"
	cb null, [args, "aaa"]

# 関数2
f2 = (args, cb) ->
	console.log "f2 : #{args}"
	cb null, 10

# 関数3
f3 = (args, cb) ->
	console.log "f3 : #{args}"
	if args is 10
		cb null, "done"
	else
		cb 'error3'

# 関数4
f4 = (args, cb) ->
	console.log "f4 : #{args}"
	cb 'error4'

cf1 = c.compose f1, f2, f3
cf1 'test', (err, res) ->
	console.log "result1 : #{err}, #{res}"

console.log '-----'

cf2 = c.compose f1, f4, f2, f3
cf2 'test', (err, res) ->
	console.log "result2 : #{err}, #{res}"

console.log '-----'

cf3 = c.compose f2, f1, f3
cf3 'test', (err, res) ->
	console.log "result3 : #{err}, #{res}"

console.log '-----'

cf4 = c.compose f4, f2
cf4 'test', (err, res) ->
	console.log "result4 : #{err}, #{res}"

console.log '-----'

cf5 = c.compose f2, f1
cf5 'test', (err, res) ->
	console.log "result5 : #{err}, #{res}"

console.log '-----'

cf6 = c.compose()
cf6 'test', (err, res) ->
	console.log "result6 : #{err}, #{res}"

console.log '-----'

cf7 = c.compose f1
cf7 'test', (err, res) ->
	console.log "result7 : #{err}, #{res}"

