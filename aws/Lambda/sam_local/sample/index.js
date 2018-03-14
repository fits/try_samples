
exports.handler = (event, context, callback) => {

    console.log(`*** INFO: ${JSON.stringify(event)}`)

    callback(null, `sample: ${JSON.stringify(event)}`)
}
