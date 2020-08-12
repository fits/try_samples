
class MessageBox {
    #messages = []
    #resolves = []

    publish(value) {
        if (this.#resolves.length > 0) {
            this.#resolves.shift()({ value })
        }
        else {
            this.#messages.push(value)
        }
    }

    [Symbol.asyncIterator]() {
        return {
            next: () => {
                console.log('*** asyncIterator next')

                return new Promise(resolve => {
                    if (this.#messages.length > 0) {

                        const value = this.#messages.shift()

                        resolve({ value })
                    }
                    else {
                        this.#resolves.push(resolve)
                    }
                })
            }
        }
    }
}

const run = async () => {
    const box = new MessageBox()

    box.publish('one')
    box.publish('two')
    box.publish('three')

    for await (const msg of box) {
        console.log(msg)
    }
}

run().catch(err => console.error(err))
