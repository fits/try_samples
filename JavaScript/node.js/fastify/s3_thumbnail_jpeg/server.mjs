
import Fastify from 'fastify'
import { S3Client, GetObjectCommand } from '@aws-sdk/client-s3'
import sharp from 'sharp'

const port = 8080

const bucket = process.env.BUCKET_NAME
const endpoint = process.env.S3_ENDPOINT

const client = new S3Client({ endpoint })

const fastify = Fastify({})

const toBuffer = async (stream) => new Promise((resolve, reject) => {
    const cs = []

    stream.on('data', c => cs.push(c))
    stream.on('end', () => resolve(Buffer.concat(cs)))
    stream.on('error', e => reject(e))
})

fastify.get('/*', async (req, reply) => {
    const path = req.params['*']
    const { w, h, q } = req.query

    const width = w ? parseInt(w) : null
    const height = h ? parseInt(h) : null
    const quality = parseInt(q ?? '80')

    if (!path.toLowerCase().endsWith('.jpg')) {
        reply.code(404).send()
        return
    }

    const t1 = Date.now()

    const { Body: res } = await client.send(new GetObjectCommand({
        Bucket: bucket,
        Key: path
    }))

    const buf = await toBuffer(res)

    const t2 = Date.now()
    console.log(`s3 download: ${t2 - t1} ms`)

    if (width == null && height == null) {
        reply.header('Content-Type', 'image/jpeg').send(buf)
        return
    }

    const output = await sharp(buf)
        .rotate()
        .resize({ width, height })
        .jpeg({ quality })
        .toBuffer()

    const t3 = Date.now()
    console.log(`sharp resize: ${t3 - t2} ms`)

    reply.header('Content-Type', 'image/jpeg').send(output)
})

console.log(`server start: port=${port}`)

await fastify.listen({ port })
