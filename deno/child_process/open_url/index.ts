import { exec } from 'node:child_process'

const urlStr = Deno.args[0]

const protocols = ['http:', 'https:']

try {
    const url = new URL(urlStr)

    if (!protocols.includes(url.protocol)) {
        throw new Error(`invalid protocol (url=${url})`)
    }

    const command = `open ${url}`

    exec(command, (err, stdout, stderr) => {
        if (err) {
            console.error(err)
            return
        }
        if (stderr) {
            console.error(stderr)
        }
        if (stdout) {
            console.log(stdout)
        }
    })

} catch(err) {
    console.error(err)
    Deno.exit(1)
}
