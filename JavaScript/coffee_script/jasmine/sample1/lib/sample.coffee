# サンプル

exports.create = (node) -> new Sample(node)

class Sample
	constructor: (node) ->
		@node = node

	check: (target) ->
		res = @node.check target, new Date().getFullYear()
		console.log res
