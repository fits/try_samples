const KerasJS = require('keras-js')

const modelFile = process.argv[2]
const width = parseInt(process.argv[3])
const height = parseInt(process.argv[4])

const model = new KerasJS.Model({
	filepath: modelFile,
	filesystem: true
})

model.ready()
	.then(r => {
		const data = new Float32Array(width * height * 3)

		const inputName = model.inputLayerNames[0]

		const inputLayer = model.modelLayersMap.get(inputName)
		inputLayer.shape[0] = height
		inputLayer.shape[1] = width

		model.modelLayersMap.forEach(n => {
			if (n.outputShape) {
				n.outputShape = null
				n.imColsMat = null
			}
		})

		model.resetInputTensors()

		const input = {}
		input[inputName] = data

		return model.predict(input)
	})
	.then(r => {
		console.log(r)
		
		model.modelLayersMap.forEach(n => {
			if (n.outputShape) {
				console.log(`${n.name} : ${n.outputShape}`)
			}
		})
	})
	.catch(err => console.error(err))
