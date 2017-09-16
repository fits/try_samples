
const util = require('util')
const fs = require('fs')

const readFile = util.promisify(fs.readFile)

readFile(process.argv[2])
	.then(buf => buf.toString('UTF-8'))
	.then(console.log)
	.catch(console.error)
