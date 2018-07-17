
import typescript from 'rollup-plugin-typescript2'
import resolve from 'rollup-plugin-node-resolve'

export default {
    input: './sample.ts',

    output: {
        file: 'bundle.js',
        format: 'umd'
    },

    plugins: [
        typescript(),
        resolve()
    ]
}