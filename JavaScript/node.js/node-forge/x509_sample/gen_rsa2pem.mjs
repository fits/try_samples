import forge from 'node-forge'
const { pki } = forge

const keys = pki.rsa.generateKeyPair({bits: 2048})

console.log( pki.publicKeyToPem(keys.publicKey) )
console.log( pki.privateKeyToPem(keys.privateKey) )
