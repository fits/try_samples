
import peggy from 'https://cdn.skypack.dev/peggy'

const parser = peggy.generate("start = ('a' / 'b')+")

console.log(parser.parse('ababba'))
