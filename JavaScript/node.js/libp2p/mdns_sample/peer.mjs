import { createLibp2p } from 'libp2p'
import { tcp } from '@libp2p/tcp'
import { mdns } from '@libp2p/mdns'
import { noise } from '@chainsafe/libp2p-noise'
import { mplex } from '@libp2p/mplex'

const node = await createLibp2p({
    addresses: {
        listen: [ '/ip4/127.0.0.1/tcp/0' ]
    },
    transports: [ tcp() ],
    connectionEncryption: [ noise() ],
    streamMuxers: [ mplex() ],
    peerDiscovery: [ mdns() ],
})

console.log(`started: ${node.getMultiaddrs()}`)

node.addEventListener('peer:discovery', ev => {
    console.log(`discovered peer: ${JSON.stringify(ev.detail)}`)
})

node.addEventListener('peer:connect', async ev => {
    console.log(`connected peer: ${JSON.stringify(ev.detail)}`)
})

process.stdin.resume()

process.stdin.on('data', async (d) => {
    await node.stop()
    console.log('stopped')

    process.exit(0)
})
