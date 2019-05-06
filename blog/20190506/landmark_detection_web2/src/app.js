
const colors = ['rgb(255, 255, 255)', 'rgb(255, 0, 0)', 'rgb(0, 255, 0)', 'rgb(0, 0, 255)', 'rgb(255, 255, 0)', 'rgb(0, 255, 255)', 'rgb(255, 0, 255)']

const radius = 5
const imageTypes = ['image/jpeg']

const modelFile = '../model/cnn_landmark_400.bin'

const worker = new Worker('./js/bundle_worker.js')

const canvas = document.getElementsByTagName('canvas')[0]
const ctx = canvas.getContext('2d')

const infoNode = document.getElementById('landmarks')

const loadDialog = document.getElementById('load-dialog')
const detectDialog = document.getElementById('detect-dialog')
const errorDialog = document.getElementById('error-dialog')

loadDialog.addEventListener('cancel', ev => ev.preventDefault())
detectDialog.addEventListener('cancel', ev => ev.preventDefault())
errorDialog.addEventListener('cancel', ev => ev.preventDefault())

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

const loadImage = url => new Promise(resolve => {
    const img = new Image()

    img.addEventListener('load', () => {
        canvas.width = img.width
        canvas.height = img.height

        ctx.clearRect(0, 0, canvas.width, canvas.height)

        ctx.drawImage(img, 0, 0)

        const d = ctx.getImageData(0, 0, canvas.width, canvas.height)
        resolve({width: img.width, height: img.height, data: imgToArray(d)})
    })

    img.src = url
})

const drawLandmarks = lms => {
    Object.values(lms).forEach(v => {
        ctx.fillStyle = colors[v.landmark]
        ctx.beginPath()
        ctx.arc(v.x, v.y, radius, 0, Math.PI * 2, false)
        ctx.fill()
    })
}

const clearLandmarksInfo = () => infoNode.innerHTML = ''

const showLandmarksInfo = lms => {
    const tableRow = v => `
      <tr>
        <td style="background: ${colors[v.landmark]}">${v.landmark}</td>
        <td>(${v.x}, ${v.y})</td>
        <td>${v.prob}</td>
      </tr>
    `

    const rowsHtml = Object.values(lms)
                            .reduce((acc, v) => acc + tableRow(v), '')

    infoNode.innerHTML = `
      <table>
        <tr>
          <th>landmark</th>
          <th>coordinate</th>
          <th>prob</th>
        </tr>
        ${rowsHtml}
      </table>
    `
}

const ready = () => {
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

        const file = ev.dataTransfer.files[0]

        if (imageTypes.includes(file.type)) {
            clearLandmarksInfo()

            const reader = new FileReader()

            reader.onload = ev => {
                loadImage(reader.result)
                    .then(d => {
                        detectDialog.showModal()
                        worker.postMessage({type: 'predict', input: d.data, width: d.width, height: d.height})
                    })
            }

            reader.readAsDataURL(file)
        }
    }, false)
}


worker.onmessage = ev => {
    if (ev.data.error) {
        console.error(ev.data.error)

        errorDialog.showModal()

        loadDialog.close()
        detectDialog.close()
    }
    else {
        switch (ev.data.type) {
            case 'init':
                ready()
                loadDialog.close()
                break
            case 'predict':
                const res = ev.data.output

                console.log(res)

                detectDialog.close()

                drawLandmarks(res)
                showLandmarksInfo(res)

                break
        }
    }
}

loadDialog.showModal()

worker.postMessage({type: 'init', url: modelFile})
