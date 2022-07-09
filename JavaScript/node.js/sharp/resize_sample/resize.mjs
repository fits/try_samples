import sharp from 'sharp'

const file = process.argv[2]
const width = parseInt(process.argv[3])

const start = Date.now()

await sharp(file).resize(width).toFile('output.jpg')

console.log(`time: ${Date.now() - start} ms`)
