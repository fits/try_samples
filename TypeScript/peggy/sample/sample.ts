import * as peggy from 'peggy'

const parser = peggy.generate("start = ('a' / 'b')+")

console.log(parser.parse('ababba'))

try {
    parser.parse('abc')
} catch(e) {
    console.error(e)
}
