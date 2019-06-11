
const r = [1, 2, 3].reduce(
    async (acc, v) => (await acc) + v, 
    0
)

r.then(v => console.log(v))
