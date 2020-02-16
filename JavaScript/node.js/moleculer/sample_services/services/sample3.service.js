
module.exports = {
    name: 'sample3',
    events: {
        'sample.*'(ctx) {
            const event = ctx.params
            console.log(`sample3: ${JSON.stringify(event)}`)
        }
    }
}
