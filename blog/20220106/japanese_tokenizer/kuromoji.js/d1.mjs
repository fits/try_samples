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
        const pos = [t.pos, t.pos_detail_1, t.pos_detail_2, t.pos_detail_3]

        console.log(`term=${t.surface_form}, partOfSpeech=${pos}`)
    }
})
