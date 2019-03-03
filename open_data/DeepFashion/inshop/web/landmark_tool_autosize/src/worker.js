
import KerasJS from 'keras-js'
import { gemv } from 'ndarray-blas-level2'
import ops from 'ndarray-ops'

let model = null

const loadModel = file => {
    const model = new KerasJS.Model({ filepath: file })

    return model.ready().then(r => model)
}

KerasJS.layers.Dense.prototype._callCPU = function(x) {
    const h = x.tensor.shape[0]
    const w = x.tensor.shape[1]

    this.output = new KerasJS.Tensor([], [h, w, this.units])

    for (let i = 0; i < h; i++) {
        for (let j = 0; j < w; j++) {

            const xt = x.tensor.pick(i, j)
            const ot = this.output.tensor.pick(i, j)

            if (this.use_bias) {
                ops.assign(ot, this.weights['bias'].tensor)
            }

            gemv(1, this.weights['kernel'].tensor.transpose(1, 0), xt, 1, ot)

            this.activationFunc({tensor: ot})
        }
    }
}

const range = n => {
    const res = []

    for (let i = 0; i < n; i++) {
        res.push(i)
    }

    return res
}

const summary = (ts) => {
    const res = []

    for (let h = 0; h < ts.tensor.shape[0]; h++) {
        for (let w = 0; w < ts.tensor.shape[1]; w++) {
            const t = ts.tensor.pick(h, w)

            const topCt = range(t.shape[0]).reduce(
                (acc, i) => {
                    const prob = t.get(i)

                    if (prob > acc.prob) {
                        acc = new Object({index: i, prob: prob, x: w, y: h})
                    }

                    return acc
                }, 
                new Object({index: -1, prob: 0, x: -1, y: -1})
            )

            if (!res[topCt.index] || topCt.prob > res[topCt.index].prob) {
                res[topCt.index] = topCt
            }
        }
    }

    return res
}


onmessage = ev => {
    switch (ev.data.type) {
        case 'init':
            loadModel(ev.data.url)
                .then(m => {
                    model = m
                    postMessage({type: ev.data.type})
                })
                .catch(err => {
                    console.log(err)
                    postMessage({type: ev.data.type, error: err})
                })

            break
        case 'predict':
            const inputName = model.inputLayerNames[0]
            const outputName = model.outputLayerNames[0]

            const w = ev.data.width
            const h = ev.data.height

            const inputLayer = model.modelLayersMap.get(inputName)
            inputLayer.shape[0] = h
            inputLayer.shape[1] = w

            model.modelLayersMap.forEach(n => {
                if (n.outputShape) {
                    n.outputShape = null
                    n.imColsMat = null
                }
            })

            model.resetInputTensors()

            const data = {}
            data[inputName] = ev.data.input

            Promise.resolve(model.predict(data))
                .then(r => {
                    const shape = model.modelLayersMap.get(outputName).output.tensor.shape
                    return new KerasJS.Tensor(r[outputName], shape)
                })
                .then(summary)
                .then(r => {
                    postMessage({type: ev.data.type, id: ev.data.id, result: r})
                })
                .catch(err => {
                    console.log(err)
                    postMessage({type: ev.data.type, id: ev.data.id, error: err})
                })

            break
    }
}
