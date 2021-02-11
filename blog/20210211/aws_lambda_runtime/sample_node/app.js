
exports.handler = async (event, context) => {
    console.log('*** call handler')
    console.log(`event = ${JSON.stringify(event)}`)
    console.log(`context = ${JSON.stringify(context)}`)
    console.log(`remaining time = ${context.getRemainingTimeInMillis()}`)

    return 'sample-node'
}