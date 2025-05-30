import sharp from 'sharp'

const input = Uint8Array.from([
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    255, 255, 0, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 255, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
    0, 255, 0, 255,
])

const img = sharp(input, {
    raw: {
        width: 6,
        height: 6,
        channels: 4,
    }
})

await img.toFile('output.png')
