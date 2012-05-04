
csv = require 'ya-csv'

reader = csv.createCsvFileReader 'test.csv'

reader.on 'data', (data) ->
	console.log data
	console.log data[2]

