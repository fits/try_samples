
try
	os = require 'os'
	console.log os.hostname()
catch e
	hostName = process.env.HOST || process.env.HOSTNAME
	console.log "err: #{hostName}"
