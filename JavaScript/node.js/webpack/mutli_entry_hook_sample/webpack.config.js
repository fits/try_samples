
const fs = require('fs')
const path = require('path')

const NodeZip = require('node-zip')

const doZip = (destFile, item) => {
    const zip = new NodeZip()

    zip.file(item.entry, item.data)

    const data = zip.generate({ base64: false, compression: 'DEFLATE' })

    fs.writeFileSync(destFile, data, 'binary')
}

module.exports = {
    entry: {
        a: './src/a.js',
        b: './src/b.js'
    },
    output: {
        filename: '[name]/index.js',
        path: path.resolve(__dirname, 'dist')
    },
    plugins: [
        new Object({
            apply: compiler => {
                compiler.hooks.done.tap('zip', r => {
                    const outputPath = r.compilation.outputOptions.path

                    Object.entries(r.compilation.assets).forEach( ([k, v]) => {
                        doZip(
                            path.resolve(outputPath, `${k.split('.')[0]}.zip`),
                            {entry: 'index.js', data: v._cachedSource}
                        )
                    })
                })
            }
        })
    ]
}
