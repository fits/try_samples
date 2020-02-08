
const run = async () => {
    const wasm = await import('./pkg/sample_promise.js')

    const rs = await Promise.all(['a1', 'b2', 'c3'].map(wasm.sample))

    console.log(rs)

    rs.forEach(r =>
        document.getElementById('res').innerHTML += `<p>${r}</p>`
    )
}

run().catch(err => console.error(err))
