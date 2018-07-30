
const {FuseBox} = require('fuse-box')

const fuse = FuseBox.init({
    output: '$name.js'
})

fuse.bundle('bundle').instructions('> *.ts')

fuse.run()
