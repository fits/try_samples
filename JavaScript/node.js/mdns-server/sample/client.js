const mdns = require('mdns-server')({
    noInit: true,
})

const service = process.argv[2]
const name = `${service}.local`

mdns.on('response', (resp) => {
    const ans = resp.answers[0]

    if (ans.name === name) {
        console.log(JSON.stringify(resp))

        mdns.destroy()
    }
})

mdns.on('ready', () => {
    mdns.query({
        questions: [{
            name,
            type: 'PTR'
        }]
    })
})

mdns.initServer()
