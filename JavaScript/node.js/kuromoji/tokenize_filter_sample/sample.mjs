import kuromoji from 'kuromoji'
import moji from 'moji'

const dicPath = 'node_modules/kuromoji/dict'
const msg = process.argv[2]

const excPos = [
    '接続詞', '助詞', '助動詞', '記号', 'その他', 'フィラー', 
    '連体詞', '感動詞', '副詞'
]

kuromoji.builder({ dicPath }).build((err, tokenizer) => {
    if (err) {
        console.error(err)
        return
    }

    const str = moji(msg)
        .convert('ZEtoHE')
        .convert('ZStoHS')
        .convert('HKtoZK')
        .toString()

    const tokens = tokenizer
        .tokenize(str)
        .filter(t => !excPos.includes(t.pos))

    for (const t of tokens) {
        console.log(t)
    }
})
