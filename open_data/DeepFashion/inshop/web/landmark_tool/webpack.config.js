
module.exports = {
    entry: {
        bundle: __dirname + '/src/app.js',
        bundle_worker: __dirname + '/src/worker.js'
    },
    output: {
        path: __dirname + '/js',
        filename: '[name].js',
    },
    node: {
        fs: 'empty'
    }
}
