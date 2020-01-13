
const timeout = 3000

const clusterId = 'test-cluster'
const clientId = process.argv[2]
const subject = process.argv[3]

const stan = require('node-nats-streaming').connect(clusterId, clientId)

const latestSeq = (subject) => new Promise((resolve, revoke) => {

    const opts = stan.subscriptionOptions().setStartWithLastReceived()
    const sub = stan.subscribe(subject, opts)

    sub.on('error', revoke)

    let timeoutId = null

    sub.on('ready', () => {
        console.log('latestSeq ready')

        timeoutId = setTimeout(
            () => {
                sub.unsubscribe()
                revoke(new Error('latestSeq timeout'))
            },
            timeout
        )
    })

    sub.on('message', msg => {
        console.log('latestSeq recieve message')

        if (timeoutId) {
            clearTimeout(timeoutId)
        }

        sub.unsubscribe()

        resolve(msg.getSequence())
    })
})

const readMessages = (subject, endSeq, startSeq = 0) => 
    new Promise((resolve, revoke) => {
        const opts = stan.subscriptionOptions().setStartAtSequence(startSeq)
        const sub = stan.subscribe(subject, opts)

        sub.on('error', revoke)

        const res = []

        sub.on('message', msg => {
            res.push(msg.getData())

            if (msg.getSequence() >= endSeq) {
                sub.unsubscribe()

                resolve(res)
            }
        })
    })

const run = async () => {
    const seqNo = await latestSeq(subject)

    console.log(seqNo)

    const msgs = await readMessages(subject, seqNo)

    console.log(msgs)
}

stan.on('connect', () => {

    run()
        .catch(err => console.error(err))
        .finally(() => stan.close())

})
