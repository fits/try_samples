
exports.handler = async (event, context) => {
    console.log(`* handler: event = ${JSON.stringify(event)}`)
    console.log(`* handler: context = ${JSON.stringify(context)}`)
    console.log(`* remaining time: ${context.getRemainingTimeInMillis()}`)

    return 'hello'
}