
module.exports = {
    name: 'sample3',
    events: {
        'sample.*'(event) {
            console.log(`sample3: ${JSON.stringify(event)}`)
        }
    }
}
