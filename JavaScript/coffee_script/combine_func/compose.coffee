
# 関数をコールバックで繋げて処理する無名関数を返す
exports.compose = (funcs...) ->
	(args, callback) ->
		cb = genCallback callback

		if funcs?.length > 0
			for i in [(funcs.length - 1)..0]
				cb = genCallback cb, funcs[i]

		cb null, args

genCallback = (callback, func) ->
	(err, res) ->
		if err?
			callback err
		else if func?
			func res, callback
		else
			callback null, res
