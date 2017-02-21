module.exports = {
	entry: './index.js',
	output: {
		filename: 'bundle.js',
		path: './build'
	},
	module: {
		rules: [
			{
				test: /\.js$/,
				exclude: /node_modules/,
				loader: 'babel-loader',
				options: {
					presets: ['es2015']
				}
			}
		]
	}
}