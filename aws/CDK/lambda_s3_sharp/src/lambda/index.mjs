
import { S3Client, GetObjectCommand } from '@aws-sdk/client-s3'
import sharp from 'sharp'

const endpoint = process.env.S3_ENDPOINT
const bucket = process.env.BUCKET_NAME

const client = new S3Client({ endpoint })

const notFound = {
    statusCode: 404
}

const serverError = {
    statusCode: 500
}

const toBuffer = async (stream) => new Promise((resolve, reject) => {
    const cs = []

    stream.on('data', c => cs.push(c))
    stream.on('end', () => resolve(Buffer.concat(cs)))
    stream.on('error', e => reject(e))
})

const toResponse = buf => {
    return {
        headers: { 'Content-Type': 'image/jpeg' },
        statusCode: 200,
        body: buf.toString('base64'),
        isBase64Encoded: true
    }
}

export const handler = async (event) => {
    const key = event.rawPath.slice(1)
    const { w, h, q } = event.queryStringParameters ?? {}

    const width = w ? parseInt(w) : null
    const height = h ? parseInt(h) : null
    const quality = parseInt(q ?? '80')

    if (!key.endsWith('.jpg')) {
        return notFound
    }

    try {
        const { Body: st } = await client.send(new GetObjectCommand({
            Bucket: bucket,
            Key: key
        }))
    
        const buf = await toBuffer(st)
    
        if (width == null && height == null) {
            return toResponse(buf)
        }
    
        const output = await sharp(buf)
            .rotate()
            .resize({ width, height })
            .jpeg({ quality })
            .toBuffer()
    
        return toResponse(output)

    } catch(e) {
        if (e.Code == 'NoSuchKey') {
            return notFound
        }
        else {
            return serverError
        }
    }
}
