
const KerasJS = require('keras-js')
const jsonDict = require('./data/dict.json')

const inputWords = process.argv[2].split(' ')
const windowSize = inputWords.length
const textNum = 10

const [idx2word, word2idx] = jsonDict.reduce(
	(acc, v) => {
		acc[0][v.index] = v.word
		acc[1][v.word] = v.index

		return acc
	},
	[{}, {}]
)

const model = new KerasJS.Model({
	filepath: './data/cnn-sample.bin',
	filesystem: true
})

const range = num => Array.from(Array(num))

const shuffle = ds => {
	const res = ds.slice()

	for(let i = res.length - 1; i > 0; i--){
		const r = Math.floor(Math.random() * (i + 1))
		const tmp = res[i]
		res[i] = res[r]
		res[r] = tmp
	}

	return res
}

const argmax = ds => ds.reduce(
	(acc, v, i) => (v > acc.prob) ? {index: i, prob: v} : acc,
	{index: -1, prob: 0}
)

const randomChoice = ds => {
	const list = shuffle(
		Array.from(ds).map((v, i) => new Object({ index: i, prob: v }))
	)

	const res = list.reduce((acc, v) => {
		if (acc[1] == null) {
			acc[0] -= v.prob

			if (acc[0] <= 0) {
				acc[1] = v
			}
		}
		return acc
	}, [Math.random(), null])[1]

	return res ? res : list[list.length - 1]
}

const generateText = (model, words, size) =>
	range(size).reduce(
		(acc, i) =>
			acc.then( ws => {
				if (ws[ws.length - 1] == '\n') {
					return ws
				}

				const d = range(windowSize).reduce(
					(acc, v, i) => [word2idx[ws[ws.length - i - 1]]].concat(acc),
					[]
				)

				return model
						.predict({ input_1: new Float32Array(d) })
						.then( res => 
							Math.random() <= 0.3 ?
								argmax(res.dense_1) : randomChoice(res.dense_1)
						)
						.then( nc => ws.concat(idx2word[nc.index]) )
			})
		, 
		Promise.resolve(words.slice())
	)

model
	.ready()
	.then(() =>
		range(textNum).reduce(
			(acc, i) =>
				acc.then(res =>
					generateText(model, inputWords, 50)
						.then(ts => res.concat(ts.join('').trim()))
				)
			,
			Promise.resolve([])
		)
	)
	.then(res => res.forEach(v => console.log(v)))
	.catch(err => console.error(err))
