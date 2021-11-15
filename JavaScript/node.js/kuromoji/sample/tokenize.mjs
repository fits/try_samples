import kuromoji from 'kuromoji'

const dicPath = 'node_modules/kuromoji/dict'
const text = process.argv[2]

kuromoji.builder({ dicPath }).build((err, tokenizer) => {
    if (err) {
        console.error(err)
        return
    }

    const ts = tokenizer.tokenize(text)

    for (const t of ts) {
        console.log(t)
    }
})
