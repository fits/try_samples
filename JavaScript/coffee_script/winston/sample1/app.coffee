
winston = require 'winston'

winston.add winston.transports.File,
	filename: 'logs/app.log'
	timestamp: true
	json: false

winston.info "test message", {name: 'test'}

winston.warn "*** warning"

winston.error "***** ERROR *****"

