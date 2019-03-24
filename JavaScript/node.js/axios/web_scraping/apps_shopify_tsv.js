
const fs = require('fs')
const readline = require('readline')

const SEP = '\t'

const file = process.argv[2]

const f = readline.createInterface(fs.createReadStream(file))

console.log(['title', 'rating', 'review', 'price-min', 'price-max'].join(SEP))

f.on('line', line => {
    const obj = JSON.parse(line)

    const title = obj.title.split('–')[0].trim()

    const rating = obj.rating
    const review = obj.review

    const pricing = obj.pricing.map(p => {
        if (p.toLowerCase().includes('free')) {
            return 0.0
        }
        else if (p.trim().startsWith('$')) {
            return parseFloat(p.trim().substring(1))
        }

        return NaN
    })

    const minPricing = Math.min.apply(null, pricing)
    const maxPricing = Math.max.apply(null, pricing)

    console.log([title, rating, review, minPricing, maxPricing].join(SEP))
})
