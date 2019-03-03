
const colors = ['rgb(255, 255, 255)', 'rgb(255, 0, 0)', 'rgb(0, 255, 0)', 'rgb(0, 0, 255)', 'rgb(255, 255, 0)', 'rgb(0, 255, 255)', 'rgb(255, 0, 255)']

const modelFile = '../model/cnn_landmark.bin'

const pSize = 5

const canvas = document.getElementsByTagName('canvas')[0]
const ctx = canvas.getContext('2d')
const msg = document.getElementById('msg')
const resultNode = document.getElementById('result')

const worker = new Worker('./js/bundle_worker.js')

let currentId = null

const message = s => msg.textContent = s

const imgToArray = imgData => new Float32Array(
    imgData.data.reduce(
        (acc, v, i) => {
            if (i % 4 != 3) {
                acc.push(v)
            }
            return acc
        },
        []
     )
)

const loadImage = file => new Promise((resolve) => {
    const img = new Image()

    img.addEventListener('load', () => {
        canvas.width = img.width
        canvas.height = img.height

        ctx.clearRect(0, 0, canvas.width, canvas.height)

        ctx.drawImage(img, 0, 0)

        const d = ctx.getImageData(0, 0, canvas.width, canvas.height)
        resolve({width: img.width, height: img.height, data: imgToArray(d)})
    })

    img.src = file
})

const drawLandmarks = ps => {
    ps.forEach((v, i) => {
        if (i > 0) {
            ctx.fillStyle = colors[i]
            ctx.beginPath()
            ctx.arc(v.x, v.y, pSize, 0, Math.PI * 2, false)
            ctx.fill()
        }
    })
}

const showResult = ps => {
    const html = ps.reduce(
        (acc, v, i) => {
            if (i > 0) {
                acc += `<tr class="landmark${i}"><td>${i}</td><td>(${v.x}, ${v.y})</td><td>${v.prob}</td></tr>`
            }
            return acc
        }, 
        ''
    )

    resultNode.innerHTML = '<table><tr><th>landmark</th><th>coordinate</th><th>prob</th></tr>' + html + '</table>'
}

const createId = () => Math.random().toString().substring(2)

const ready = () => {
    message('ready')

    canvas.addEventListener('dragover', ev => {
        ev.preventDefault()
        canvas.classList.add('dragging')
    }, false)

    canvas.addEventListener('dragleave', ev => {
        canvas.classList.remove('dragging')
    }, false)

    canvas.addEventListener('drop', ev => {
        ev.preventDefault()
        canvas.classList.remove('dragging')

        message('detecting ...')

        const file = ev.dataTransfer.files[0]

        if (file.type != 'image/jpeg') {
            message('not supported image type')
        }
        else {
            resultNode.innerHTML = ''

            const reader = new FileReader()

            reader.onload = ev => {
                loadImage(reader.result)
                    .then(d => {
                        const id = createId()
                        currentId = id

                        worker.postMessage({type: 'predict', id: id, input: d.data, width: d.width, height: d.height})
                    })
                    .catch(err => {
                        message(err.message)
                        console.error(err)
                    })
            }

            reader.readAsDataURL(file)
        }
    }, false)
}

const donePredict = res => {
    message('completed')

    drawLandmarks(res)
    showResult(res)

    console.log(res)
}


worker.onmessage = ev => {
    if (ev.data.error) {
        console.error(ev.data.error)
        message(ev.data.error.message)
    }
    else {
        switch (ev.data.type) {
            case 'init':
                ready()
                break
            case 'predict':
                if (ev.data.id == currentId) {
                    donePredict(ev.data.result)
                }
                else {
                    console.log(`miss match: currentId=${currentId}, result id=${ev.data.id}`)
                }

                break
        }
    }
}

message('loading model ...')

worker.postMessage({type: 'init', url: modelFile})
