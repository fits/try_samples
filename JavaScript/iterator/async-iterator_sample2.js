
class MessageBox {
    #messages = []
    #resolves = []

    publish(msg) {
        if (this.#resolves.length > 0) {
            this.#resolves.shift()(msg)
        }
        else {
            this.#messages.push(msg)
        }
    }

    [Symbol.asyncIterator]() {
        return {
            next: () => {
                console.log('*** asyncIterator next')

                return new Promise(resolve => {
                    if (this.#messages.length > 0) {

                        const value = this.#messages.shift()

                        resolve({ value, done: false })
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
