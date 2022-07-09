import JSZip from 'jszip'
import fetch from 'node-fetch'
import { writeFile } from 'fs/promises'

process.stdin.resume()

const fileName = url => {
    const r = url.split('/')
    return r[r.length - 1]
}

process.stdin.on('data', async (chunk) => {
    const urls = chunk.toString().split('\n').map(s => s.trim()).filter(s => s.length > 0)

    const zip = new JSZip()

    for (const url of urls) {
        const b = await fetch(url).then(r => r.body)
        zip.file(fileName(url), b, {binary: true})
    }

    const content = await zip.generateAsync({type: 'nodebuffer'})
    await writeFile('output.zip', content)
})
