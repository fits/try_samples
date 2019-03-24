
const axios = require('axios')

const jsdom = require('jsdom')
const { JSDOM } = jsdom

if (process.argv.length < 4) {
    return
}

const category = process.argv[2]
const page = parseInt(process.argv[3])

const baseUrl = `https://apps.shopify.com/browse/${category}?page=${page}`

const title = doc => 
    doc.querySelector('meta[property="og:title"]').content

const rating = doc => parseFloat(
    doc.querySelector('.ui-star-rating__rating').firstChild.textContent
)

const review = doc => {
    const parseNum = node => 
        parseInt(node.textContent.substring(1, node.textContent.indexOf(' ')))

    const el = doc.querySelector('[href="#reviews"]')

    return (el == null) ? 0 : parseNum(el)
}

const pricing = doc =>
    Array.from(doc.querySelectorAll('.pricing-plan-card__title-header'))
        .map(n => n.textContent.trim())

const scrapeItem = url =>
    axios.get(url)
        .then(res => new JSDOM(res.data).window.document)
        .then(doc =>
            new Object({
                title: title(doc),
                rating: rating(doc),
                review: review(doc),
                pricing: pricing(doc)
            })
        )

axios.get(baseUrl)
    .then(res => new JSDOM(res.data).window.document)
    .then(doc =>
        Array.from(doc.querySelectorAll('a.ui-app-card'))
            .map(a => a.href)
    )
    .then(urls => Promise.all(urls.map(url => scrapeItem(url))))
    .then(ds => ds.forEach(d => console.log(JSON.stringify(d))))
    .catch(err => console.error(err))
