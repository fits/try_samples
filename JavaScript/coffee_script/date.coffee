
now = new Date()

console.log now
console.log now.getTime()

console.log "#{now.getFullYear()}/#{now.getMonth() + 1}/#{now.getDate()}"

m = now.getMonth() + 1
m = "0#{m}" if m < 10

d = now.getDate()
d = "0#{d}" if d < 10

console.log "#{now.getFullYear()}#{m}#{d}"

console.log "-----------------------"

dateString = (d = new Date()) ->
	"#{d.getFullYear()}#{padZero d.getMonth() + 1}#{padZero d.getDate()}"

padZero = (num) ->
	z = if num < 10 then '0' else ''
	"#{z}#{num}"

console.log dateString()
console.log dateString(new Date(2012, 7, 11))
