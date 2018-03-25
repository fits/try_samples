
import * as k8s from '@kubernetes/client-node'

if (!process.env.HOME) {
    process.env.HOME = process.env.USERPROFILE
}

const api = k8s.Config.defaultClient()

console.log(api.basePath)

const main = async () => {
    const res = await api.listNamespacedPod('default')

    console.log(res.body)

    res.body.items.forEach(v => {
        console.log(`--- item: ${JSON.stringify(v.metadata)}`)
    })
}

main().catch(err => console.error(`ERROR: ${err}`))
