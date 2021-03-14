
exports.handler = async (event) => {
    console.log(`*** handler: ${JSON.stringify(event)}`)

    return { id: event.id, value: 234 }
}