
const sleep = timeout => new Promise(resolve => setTimeout(resolve, timeout))

const delay = async (func) => {
    console.log('start')

    await sleep(3000)

    func()
}

delay(() => console.log('action'))
    .catch(err => console.error(err))
