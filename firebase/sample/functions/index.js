const functions = require('firebase-functions')

exports.items = functions.https.onRequest((req, res) => {
    const data = [
        {name: 'item1', qty: 1},
        {name: 'item2', qty: 2},
        {name: 'item3', qty: 3},
        {name: 'item4', qty: 4},
        {name: 'item5', qty: 5}
    ]

    res.send(JSON.stringify(data))
})
