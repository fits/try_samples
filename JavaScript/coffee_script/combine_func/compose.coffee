
exports.compose = (funcs...) ->
	(args, callback) ->
		cb = genCallback callback

		for f in funcs.reverse()
			cb = genCallback cb, f

		cb null, args

genCallback = (callback, func) ->
	(err, res) ->
		if err?
			callback err
		else if func?
			func res, callback
		else
			callback null, res
