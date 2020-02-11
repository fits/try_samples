
module.exports = {
    name: 'sample1',
    actions: {
        async create(ctx) {
            console.log(`sample1.create: ${JSON.stringify(ctx.params)}`)

            const res = await ctx.call('sample2.command')

            console.log(`sample2.command result: ${res}`)

            ctx.emit('sample.created', {
                revision: 1,
                value: ctx.params.value
            })
        }
    }
}
