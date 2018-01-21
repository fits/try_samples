
function isoWeek(date) {
	const thDate = new Date(date.getFullYear(), date.getMonth(), date.getDate())
	const offset = 4 - (date.getDay() == 0 ? 7 : date.getDay())

	thDate.setDate(thDate.getDate() + offset)

	const year = thDate.getFullYear()

	const days = (thDate - new Date(year, 0, 1)) / 86400000
	return [year, Math.ceil((days + 1) / 7)]
}

for (i = 2010; i < 2025; i++) {

	const d1 = new Date(`${i}-01-01`)
	const d2 = new Date(`${i}-12-28`)

	console.log(d1.toLocaleString() + " : " + isoWeek(d1))
	console.log(d2.toLocaleString() + " : " + isoWeek(d2))
}
