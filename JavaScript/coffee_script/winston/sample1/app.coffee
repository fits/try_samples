
winston = require 'winston'

winston.add winston.transports.File,
	filename: 'logs/app.log'
	timestamp: true
	json: false

winston.info "test message"

winston.warn "*** warning"

winston.error "***** ERROR *****"

