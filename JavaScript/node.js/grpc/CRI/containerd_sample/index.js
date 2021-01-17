
const protoLoader = require('@grpc/proto-loader')
const grpc = require('@grpc/grpc-js')

const protoFile = './proto/v1alpha2/api.proto'
const address = 'unix:///run/containerd/containerd.sock'
//const address = 'unix:///var/snap/docker/current/run/docker/containerd/containerd.sock'

const pd = protoLoader.loadSync(protoFile, {})
const proto = grpc.loadPackageDefinition(pd)

const runtimeService = new proto.runtime.v1alpha2.RuntimeService(
    address,
    grpc.credentials.createInsecure()
)

const imageService = new proto.runtime.v1alpha2.ImageService(
    address,
    grpc.credentials.createInsecure()
)

const promisify = (obj, method) => args =>
    new Promise((resolve, reject) => {
        obj[method](args, (err, res) => {
            if (err) {
                reject(err)
            }
            else {
                resolve(res)
            }
        })
    })

const version = promisify(runtimeService, 'Version')

const listPodSandbox = promisify(runtimeService, 'ListPodSandbox')
const runPodSandbox = promisify(runtimeService, 'RunPodSandbox')

const listImages = promisify(imageService, 'ListImages')

const run = async () => {
    const v = await version({})
    console.log(v)

    const im = await listImages({})
    console.log(im.images)

    const res = await runPodSandbox({config: {
        metadata: {
            name: 'sample-sandbox',
            namespace: 'default'
        },
        log_directory: '/tmp',
        linux: {}
    }})
    console.log(res)

    const pods = await listPodSandbox({})
    console.log(pods)
}

run().catch(err => console.error(err))
