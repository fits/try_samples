fs = require 'fs'

now = Math.floor(new Date().getTime() / 1000)
console.log now

fs.utimes process.argv[2], now, now, (err) -> console.log "#{err}" if err

