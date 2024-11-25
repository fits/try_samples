
const res = await fetch('https://dummyjson.com/products')
const json = await res.json()

json.products.forEach(p => {
    console.log(JSON.stringify(p))
})
