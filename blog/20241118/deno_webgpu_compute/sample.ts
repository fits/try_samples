const workGroupSize = 4

const adapter = await navigator.gpu.requestAdapter()
const device = await adapter?.requestDevice()

if (!device) {
    console.error('not found device')
    Deno.exit(0)
}

const input = new Uint32Array([1, 2, 3, 4, 5, 6, 7, 8])

console.log(`input: ${input}`)

const wgsl = `
@group(0) @binding(0)
var<storage, read_write> data: array<u32>;

@compute @workgroup_size(${workGroupSize})
fn main(@builtin(global_invocation_id) global_id: vec3<u32>) {
    data[global_id.x] = 3u * data[global_id.x] + 1u;
}
`

const shader = device.createShaderModule({
    code: wgsl
})

const pipeline = await device.createComputePipelineAsync({
    layout: 'auto',
    compute: {
        module: shader,
        entryPoint: 'main'
    }
})

const storageBuf = device.createBuffer({
    mappedAtCreation: true,
    size: input.byteLength,
    usage: GPUBufferUsage.STORAGE | GPUBufferUsage.COPY_DST | GPUBufferUsage.COPY_SRC
})
const data = new Uint32Array(storageBuf.getMappedRange())
data.set(input)
storageBuf.unmap()

const outputBuf = device.createBuffer({
    size: storageBuf.size,
    usage: GPUBufferUsage.MAP_READ | GPUBufferUsage.COPY_DST
})

const bindGroup = device.createBindGroup({
    layout: pipeline.getBindGroupLayout(0),
    entries: [
        {
            binding: 0,
            resource: {
                buffer: storageBuf
            }
        }
    ]
})

const encoder = device.createCommandEncoder()

const pass = encoder.beginComputePass()
pass.setPipeline(pipeline)
pass.setBindGroup(0, bindGroup)
pass.dispatchWorkgroups(input.length / workGroupSize)
pass.end()

encoder.copyBufferToBuffer(storageBuf, 0, outputBuf, 0, storageBuf.size)

device.queue.submit([encoder.finish()])

await outputBuf.mapAsync(GPUMapMode.READ)

const res = new Uint32Array(outputBuf.getMappedRange())

console.log(`output: ${res}`)

outputBuf.unmap()
