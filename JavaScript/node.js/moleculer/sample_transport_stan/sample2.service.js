
module.exports = {
    name: 'sample2',
    actions: {
        command(ctx) {
            console.log('called sample2.command')
            return 'ok'
        }
    },
    events: {
        'sample.created'(ctx) {
            const event = ctx.params
            console.log(`sample2: ${JSON.stringify(event)}`)
        }
    }
}
