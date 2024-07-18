const mdns = require('mdns-server')({
    noInit: true,
})

mdns.on('response', (resp) => {
    console.log(`*** on response: ${JSON.stringify(resp)}`)
})

mdns.on('query', (query) => {
    console.log(`*** on query: ${JSON.stringify(query)}`)

    const q = query.questions[0]

    if (q.type === 'PTR') {
        mdns.respond({
            answers: [
                { name: q.name, type: 'PTR', ttl: 3200, data: `sample:1234.${q.name}` }
            ],
            additionals: [
                { name: `sample:1234.${q.name}`, type: 'SRV', ttl: 3200, data: { port: 8888, target: 'sample.local' }},
                { name: 'sample.local', type: 'A', ttl: 120, data: '192.168.1.1' }
            ]
        })
    }
})

mdns.initServer()