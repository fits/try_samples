'use strict'
const k8s = require('@pulumi/kubernetes')

const appName = 's1'
const appImage = `sample/${appName}`
const appLabels = { app: appName }

const deployment = new k8s.apps.v1.Deployment(appName, {
    spec: {
        selector: { matchLabels: appLabels },
        replicas: 1,
        template: {
            metadata: { labels: appLabels },
            spec: {
                containers: [
                    { name: appName, image: appImage, 
                      imagePullPolicy: "Never", port: { containerPort: 8080 } }
                ]
            }
        }
    }
})

const service = new k8s.core.v1.Service(appName, {
    metadata: { labels: appLabels },
    spec: {
        selector: appLabels,
        ports: [{port: 8080, name: 'http'}]
    }
})


exports.name = deployment.metadata.apply(m => m.name)
