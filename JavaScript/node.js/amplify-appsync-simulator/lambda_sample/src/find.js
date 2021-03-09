
exports.handler = async (event) => {
    console.log(`*** handle: ${JSON.stringify(event)}`)

    return { id: event.id, value: 456 }
}
