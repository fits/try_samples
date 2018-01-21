
const moment = require('moment')

for (i = 2010; i < 2025; i++) {

	const d1 = new Date(`${i}-01-01`)
	const d2 = new Date(`${i}-12-28`)

	const m1 = moment(d1)
	const m2 = moment(d2)

	console.log(`${d1.toLocaleString()} : ${m1.isoWeekYear()}, ${m1.isoWeek()}`)
	console.log(`${d2.toLocaleString()} : ${m2.isoWeekYear()}, ${m2.isoWeek()}`)
}
