
const k8s = require('@kubernetes/client-node')

if (!process.env.HOME) {
    process.env.HOME = process.env.USERPROFILE
}

const api = k8s.Config.defaultClient()

console.log(api.basePath)

api.listNamespacedPod('default')
    .then(r => r.body)
    .then(b => {
        console.log(b)
        return b.items
    })
    .then(it => it.forEach(v => console.log(JSON.stringify(v.metadata))))
    .catch(err => console.error(`ERROR: ${err}`))
