
describe 'Sample', ->
	beforeEach ->
		@node =
			check: (target, year) ->

		@sample = require('../lib/sample').create(@node)


	it 'check test', ->
		spyOn(@node, 'check').andReturn true

		@sample.check 'test'

		expect(@node.check).toHaveBeenCalled()
		expect(@node.check).toHaveBeenCalledWith('test', new Date().getFullYear())


	it 'check test2', ->
		spyOn(@node, 'check').andCallFake (target, year) ->
			console.log "#{target}, #{year}"
			true

		@sample.check 'test'

		expect(@node.check).toHaveBeenCalled()
