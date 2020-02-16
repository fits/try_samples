
module.exports = {
    name: 'sample2',
    events: {
        'sample.created'(ctx) {
            const event = ctx.params
            console.log(`sample2: ${JSON.stringify(event)}`)
        }
    }
}
