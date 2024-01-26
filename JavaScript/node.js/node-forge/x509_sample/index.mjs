import forge from 'node-forge'
const { pki } = forge

const keys = pki.rsa.generateKeyPair({bits: 2048})

const cert = pki.createCertificate()

cert.publicKey = keys.publicKey

cert.validity.notBefore = new Date()

const expiryDate = new Date()
expiryDate.setFullYear(expiryDate.getFullYear() + 1)

cert.validity.notAfter = expiryDate

const attrs = [
    { name: 'commonName', value: 'example.org' },
    { name: 'countryName', value: 'US' }
]

cert.setSubject(attrs)
cert.setIssuer(attrs)

cert.sign(keys.privateKey)

const pem = pki.certificateToPem(cert)

console.log(pem)
