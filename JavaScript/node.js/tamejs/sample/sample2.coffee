###
実行手順
 > coffee -b -c sample2.coffee
 > tamejs -o sample2.js sample2.js
 > node sample2.js
###

testfunc = (id, callback) ->
	console.log id
	setTimeout callback, 5000, "1", "ok"

console.log "*** before await"

`await {`

console.log "await start"
testfunc "id:1", defer(res, msg)
console.log "await end"

`}`

console.log "*** after await"
console.log "#{res}, #{msg}"
