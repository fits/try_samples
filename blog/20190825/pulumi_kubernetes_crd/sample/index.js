'use strict'
const k8s = require('@pulumi/kubernetes')

const capitalize = s => `${s[0].toUpperCase()}${s.slice(1)}`

const crdName = 'item'
const crdGroup = 'example.com'
const crdVersion = 'v1alpha1'

const props = {
    value: 'integer',
    note: 'string'
}

const items = [
    { name: 'item1', value: 100, note: 'sample item 1' },
    { name: 'item2', value:  20, note: 'sample item 2' }
]

const crdKind = capitalize(crdName)
const crdPlural = `${crdName}s`

new k8s.apiextensions.v1beta1.CustomResourceDefinition(crdName, {
    metadata: { name: `${crdPlural}.${crdGroup}` },
    spec: {
        group: crdGroup,
        version: crdVersion,
        scope: 'Namespaced',
        names: {
            kind: crdKind,
            plural: crdPlural,
            singular: crdName
        },
        preserveUnknownFields: false,
        validation: {
            openAPIV3Schema: {
                type: 'object',
                properties: {
                    spec: {
                        type: 'object',
                        properties: Object.fromEntries(
                            Object.entries(props).map(([k, v]) => 
                                [k, { type: v }]
                            )
                        )
                    }
                }
            }
        }
    }
})

items.forEach(it => 
    new k8s.apiextensions.CustomResource(it.name, {
        apiVersion: `${crdGroup}/${crdVersion}`,
        kind: crdKind,
        metadata: {
            name: it.name
        },
        spec: Object.fromEntries(
            Object.keys(props).map(k => [k, it[k]])
        )
    })
)
