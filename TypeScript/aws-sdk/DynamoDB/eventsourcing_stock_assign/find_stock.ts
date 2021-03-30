
import { StockAction } from './stock'

const id = process.argv[2]

StockAction.find(id)
    .then(s => console.log(s))
    .catch(err => console.error(err))
