
const k8s = require('@kubernetes/client-node')

const endpoint = '/api/v1/namespaces/default/pods'

if (!process.env.HOME) {
    process.env.HOME = process.env.USERPROFILE
}

const conf = new k8s.KubeConfig()
conf.loadFromFile(`${process.env.HOME}/.kube/config`)

const w = new k8s.Watch(conf)

w.watch(
    endpoint,
    {}, 
    (type, obj) => {
        console.log(`${type} : ${obj.metadata.name}`)
    },
    err => {
        if (err) {
            console.error(err)
        }
        else {
            console.log('done')
        }
    }
)
