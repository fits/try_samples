
$( -> 
	ws = new WebSocket 'ws://localhost:9000/connect'
	ws.onmessage = (event) -> console.log "receive : #{event}, #{event.data}"
	ws.onopen = (event) -> console.log "open : #{event}"
	ws.onclose = (event) -> console.log "close : #{event}"

	$('#jobAdd').click -> $.get 'add/' + $('#title').val(), {}, (d) -> $('#jobCount').html d
	
	$(window).bind 'beforeunload', ->
		ws.onclose = ->
		ws.close()
)
