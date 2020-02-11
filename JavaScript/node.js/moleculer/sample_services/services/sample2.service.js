
module.exports = {
    name: 'sample2',
    events: {
        'sample.created'(event) {
            console.log(`sample2: ${JSON.stringify(event)}`)
        }
    }
}
