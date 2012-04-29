
if process.argv.length < 3
	console.log "#{require('path').basename(process.argv[1])} <args1>"
	process.exit()

console.log 'check ok'
