import { S3Client, GetObjectCommand } from '@aws-sdk/client-s3'
import sharp from 'sharp'

const endpoint = process.env.S3_ENDPOINT
const bucket = process.env.BUCKET_NAME

const key = process.argv[2]
const width = parseInt(process.argv[3])

const client = new S3Client({ endpoint })

const toBuffer = async (stream) => new Promise((resolve, reject) => {
    const cs = []

    stream.on('data', c => cs.push(c))
    stream.on('end', () => resolve(Buffer.concat(cs)))
    stream.on('error', e => reject(e))
})

const { Body: st } = await client.send(new GetObjectCommand({
    Bucket: bucket,
    Key: key
}))

const buf = await toBuffer(st)

const output = await sharp(buf).rotate().resize({ width }).toBuffer()

console.log(output.toString('base64'))
