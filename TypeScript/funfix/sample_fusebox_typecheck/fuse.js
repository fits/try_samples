
const {FuseBox} = require('fuse-box')
const {TypeChecker} = require('fuse-box-typechecker')

const testSync = TypeChecker({
	tsConfig: './tsconfig.json'
})

const fuse = FuseBox.init({
	output: '$name.js'
})

fuse.bundle('bundle').instructions('> *.ts')

testSync.runPromise()
	.then(n => {
		if (n != 0) {
			throw new Error(n)
		}
		return fuse.run()
	})
	.catch(console.error)
