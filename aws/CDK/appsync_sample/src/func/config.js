
const config = {}

if (process.env['DYNAMODB_ENDPOINT']) {
    config.endpoint = process.env['DYNAMODB_ENDPOINT']
}

module.exports = config
