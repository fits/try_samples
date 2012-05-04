
testFunc = (arg1, arg2, arg3, callback) ->
	if arg2 instanceof Function
		console.log '****'
		callback = arg2
		arg2 = null

	arg2 ?= 7
	arg3 ?= 9

	console.log "#{arg1}, #{arg2}, #{arg3}, #{callback}"
	callback()


testFunc 1, 2, 3, -> console.log 'callback'
testFunc 1, -> console.log 'callback'
testFunc 1, null, 3, -> console.log 'callback'

