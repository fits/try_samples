
const polka = require('polka')

polka()
    .get('/sample/:id', (req, res) => {
        const id = req.params.id
        console.log(`id: ${id}`)

        res.end('ok')
    })
    .listen(8080, err => {
        if (err) {
            throw err
        }

        console.log('started')
    })
