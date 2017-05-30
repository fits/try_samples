
const webpack = require("webpack");

module.exports = {
    entry: './index.js',
    output: {
        filename: 'bundle.js',
        path: `${__dirname}/public`
    },
    module: {
        rules: [
            {
                test: /\.tag$/, 
                exclude: /node_modules/, 
                loader: 'riotjs-loader', 
                query: { type: 'none' }
            }
        ]
    },
    plugins: [
        new webpack.ProvidePlugin({ riot: 'riot' })
    ]
}