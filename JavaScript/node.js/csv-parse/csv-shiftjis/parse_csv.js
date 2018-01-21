
const fs = require('fs')
const iconv = require('iconv-lite')
const parse = require('csv-parse')

const file = process.argv[2]

const readCsv = f => new Promise((resolve, reject) => {
	const list = []

	fs.createReadStream(f)
		.pipe(iconv.decodeStream('Shift_JIS'))
		.pipe(parse({columns: true, skip_empty_lines: true, trim: true}))
		.on('data', r => list.push(r))
		.on('end', r => resolve(list))
		.on('error', reject)
})

readCsv(file)
	.then(console.log)
	.catch(console.error)
