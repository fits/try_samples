
describe 'Sample', ->
	beforeEach ->
		@node =
			check: (target, year) ->

		@sample = require('../lib/sample').create(@node)


	it 'check test', ->
		spyOn(@node, 'check').andReturn true

		@sample.check 'test'

		# 呼び出し有無のチェック
		expect(@node.check).toHaveBeenCalled()

		# 呼び出し時の引数チェック
		expect(@node.check).toHaveBeenCalledWith('test', new Date().getFullYear())

		# 呼び出し回数のチェック
		expect(@node.check.callCount).toBe 1

		expect(@node.check.mostRecentCall.args[0]).toBe 'test'


	it 'check test2', ->
		spyOn(@node, 'check').andCallFake (target, year) ->
			console.log "#{target}, #{year}"
			true

		@sample.check 'test'

		expect(@node.check).toHaveBeenCalled()
