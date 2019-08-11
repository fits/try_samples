'use strict'
const k8s = require('@pulumi/kubernetes')

const crdKind = 'Sample'
const crdName = 'sample'
const crdPlural = `${crdName}s`
const crdGroup = 'example.com'
const crdVersion = 'v1alpha1'

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
                            info: { type: 'string' },
                            value: { type: 'integer' }
                        }
                    }
                }
            }
        }
    }
})

new k8s.apiextensions.CustomResource('sample-obj1', {
    apiVersion: `${crdGroup}/${crdVersion}`,
    kind: crdKind,
    metadata: {
        name: 'sample-obj1'
    },
    spec: { info: 'test data', value: 22 }
})
