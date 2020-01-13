
const timeout = 5000

const clusterId = 'test-cluster'
const clientId = process.argv[2]
const subject = process.argv[3]

const stan = require('node-nats-streaming').connect(clusterId, clientId)

stan.on('connect', () => {
    const lastOpts = stan.subscriptionOptions().setStartWithLastReceived()

    const last = stan.subscribe(subject, lastOpts)
    let lastSeq = 0

    const lastTimeoutId = setTimeout(() => {
        console.log('timeout')

        lastSeq = -1
        last.unsubscribe()
    }, timeout)

    last.on('message', msg => {
        clearTimeout(lastTimeoutId)

        lastSeq = msg.getSequence()

        last.unsubscribe()
    })

    last.on('unsubscribed', () => {
        console.log(`last close : seq=${lastSeq}`)

        if (lastSeq > 0) {
            const opts = stan.subscriptionOptions().setDeliverAllAvailable()
            const subsc = stan.subscribe(subject, opts)

            subsc.on('unsubscribed', () => {
                stan.close()
            })

            subsc.on('message', msg => {
                const seq = msg.getSequence()

                if (seq == lastSeq) {
                    subsc.unsubscribe()
                }

                console.log(`message: ${msg.getSequence()}, ${msg.getData()}`)
            })
        }
        else {
            stan.close()
        }
    })
})
