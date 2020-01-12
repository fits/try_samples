
const nc = require('nats').connect()

const subject = process.argv[2]

const sid = nc.subscribe(subject, msg => {
    console.log(msg)
})

nc.on('unsubscribe', (_sid, _subject) => {
    console.log(`unsubscribe sid=${_sid}, subject=${_subject}`)
    process.exit()
})

process.stdin.on('data', d => {
    nc.unsubscribe(sid)
})
