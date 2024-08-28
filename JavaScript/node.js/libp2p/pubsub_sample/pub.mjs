import { createLibp2p } from 'libp2p'
import { tcp } from '@libp2p/tcp'
import { mdns } from '@libp2p/mdns'
import { gossipsub } from '@chainsafe/libp2p-gossipsub'
import { identify } from '@libp2p/identify'
import { noise } from '@chainsafe/libp2p-noise'
import { mplex } from '@libp2p/mplex'

const topic = 'topic1'

const sleep = timeout => new Promise(resolve => setTimeout(resolve, timeout))

const node = await createLibp2p({
    addresses: {
        listen: [ '/ip4/127.0.0.1/tcp/0' ]
    },
    transports: [ tcp() ],
    connectionEncryption: [ noise() ],
    streamMuxers: [ mplex() ],
    peerDiscovery: [ mdns() ],
    services: {
        identify: identify(),
        pubsub: gossipsub({ emitSelf: true })
    }
})

console.log(`started: ${node.getMultiaddrs()}`)

node.addEventListener('peer:discovery', ev => {
    console.log(`discovered peer: ${JSON.stringify(ev.detail)}`)
})

node.addEventListener('peer:connect', ev => {
    console.log(`connected peer: ${JSON.stringify(ev.detail)}`)
})

node.services.pubsub.addEventListener('message', ev => {
    const msg = ev.detail
    const data = new TextDecoder().decode(msg.data)

    console.log(`received: topic=${msg.topic}, data=${data}`)
})

node.services.pubsub.subscribe(topic)

for (const _i of Array(5).keys()) {
    await node.services.pubsub.publish(topic, new TextEncoder().encode("message/" + new Date().toISOString()))
    await sleep(500)
}

await node.stop()

process.exit(0)
