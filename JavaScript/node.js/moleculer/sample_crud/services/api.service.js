
const HTTPServer = require('moleculer-web')

module.exports = {
    name: 'api',
    mixins: [HTTPServer],
    settings: {
        routes: [
            { aliases: { 'REST items': 'item' } }
        ]
    }
}
