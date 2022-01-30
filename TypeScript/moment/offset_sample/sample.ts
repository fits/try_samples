import moment from 'moment'

const d = '2022-01-30T16:00:00Z'

console.log(moment(d))

console.log(
    moment(d).utcOffset(9).format('YYYYMMDDHHmmss')
)

console.log(
    moment(d).utcOffset(3).format('YYYYMMDDHHmmss')
)

console.log(
    moment(d).utcOffset('+05:00').format('YYYYMMDDHHmmss')
)
