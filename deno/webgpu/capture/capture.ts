import { createCapture } from '@std/webgpu'
import { encode } from 'png'

const adapter = await navigator.gpu.requestAdapter()
const device = await adapter?.requestDevice()

if (!device) {
    console.error('not found device')
    Deno.exit(0)
}

const w = 320
const h = 240

const { texture, outputBuffer } = createCapture(device, w, h)

const encoder = device.createCommandEncoder()

encoder.beginRenderPass({
    colorAttachments: [
        {
            view: texture.createView(),
            storeOp: 'store',
            loadOp: 'clear',
            clearValue: [0, 0, 1, 1]
        }
    ]
}).end()

encoder.copyTextureToBuffer(
    { texture },
    { buffer: outputBuffer, bytesPerRow: w * 4 },
    { width: w, height: h },
)

device.queue.submit([encoder.finish()])

await outputBuffer.mapAsync(1)

const buf = new Uint8Array(outputBuffer.getMappedRange())

const img = encode(buf, w, h)

Deno.writeFileSync('./output.png', img)

outputBuffer.unmap()
