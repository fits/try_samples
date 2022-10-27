
const file = Deno.args[0]

const p = Deno.run({ cmd: ['deno', 'run', '--allow-all', file] })

setTimeout(() => {
    p.kill('SIGTERM')
    p.close()
}, 3000)
