
class MessageBox {
    #promises = []
    #resolves = []

    #appendPromise = () => this.#promises.push(
        new Promise(res => this.#resolves.push(res))
    )

    publish(msg) {
        if (this.#resolves.length == 0) {
            this.#appendPromise()
        }

        this.#resolves.shift()(msg)
    }

    [Symbol.asyncIterator]() {
        return {
            next: async () => {
                console.log('*** asyncIterator next')

                if (this.#promises.length == 0) {
                    this.#appendPromise()
                }

                const value = await this.#promises.shift()
                return { value, done: false }
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
