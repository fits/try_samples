const rp = require('request-promise-native')

const jsdom = require('jsdom')
const { JSDOM } = jsdom

const query = process.argv[2]

const url = `https://www.google.co.jp/search?q=${query}&ie=utf-8&oe=utf-8`

rp(url)
    .then(str => new JSDOM(str))
    .then(dom => dom.window.document.querySelectorAll('h3.r a'))
    .then(Array.from)
    .then(ns => ns.map(n => n.textContent))
    .then(ts => ts.forEach(t => console.log(t)))
    .catch(err => console.error(`ERROR: ${err.message}`))
