
onmessage = ev => {
    console.log('receive-worker')
    console.log(ev.data)

    postMessage(`return-${ev.data}`)
}
