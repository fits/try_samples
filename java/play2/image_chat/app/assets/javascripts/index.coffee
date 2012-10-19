
$( -> 
	$.ajaxSetup
		contentType: 'application/json; charset=UTF-8'

	ws = new WebSocket 'ws://localhost:9000/connect'
	ws.onmessage = (event) ->
		obj = JSON.parse event.data
		$('#list').append "<li><image class=\"chatimg\" src=\"#{obj.image}\" /><span class=\"msg\">#{obj.message}</span></li>"

	ws.onopen = (event) -> console.log "open : #{event}"
	ws.onclose = (event) -> console.log "close : #{event}"

	$('#sendMessage').click ->
		params =
			message: $('#message').val()
			image: $('#image').attr('src')

		$.post 'send', JSON.stringify(params), (d) ->
			console.log(d)
		, 'json'

	$(window).bind 'beforeunload', ->
		ws.onclose = ->
		ws.close()
)
