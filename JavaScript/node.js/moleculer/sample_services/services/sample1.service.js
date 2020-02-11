
module.exports = {
    name: 'sample1',
    actions: {
        create(ctx) {
            console.log(`sample1.command: ${JSON.stringify(ctx.params)}`)

            ctx.emit('sample.created', {
                revision: 1,
                value: ctx.params.value
            })
        }
    }
}
