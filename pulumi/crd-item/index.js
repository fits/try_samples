'use strict'
const k8s = require('@pulumi/kubernetes')

const capitalize = s => `${s[0].toUpperCase()}${s.slice(1)}`

const crdName = 'item'
const crdGroup = 'example.com'
const crdVersion = 'v1alpha1'

const crdKind = capitalize(crdName)
const crdPlural = `${crdName}s`

const items = [{ name: 'item1', value: 100, note: 'sample item' }]

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
        preserveUnknownFields: true,
        validation: {
            openAPIV3Schema: {
                type: 'object',
                properties: {
                    spec: {
                        type: 'object',
                        properties: {
                            value: { type: 'integer' },
                            note: { type: 'string' }
                        }
                    }
                }
            }
        }
    }
})

items.map(it => 
    new k8s.apiextensions.CustomResource(it.name, {
        apiVersion: `${crdGroup}/${crdVersion}`,
        kind: crdKind,
        metadata: {
            name: it.name
        },
        spec: { value: it.value, note: it.note }
    })
)
