import dailogPolyfill from 'dialog-polyfill/dialog-polyfill.js'
import 'dialog-polyfill/dialog-polyfill.css'

const dialog = document.querySelector('dialog')

dailogPolyfill.registerDialog(dialog)

document.getElementById('btn').addEventListener('click', ev => {
    dialog.showModal()
    setTimeout(() => dialog.close(), 5000)
})
