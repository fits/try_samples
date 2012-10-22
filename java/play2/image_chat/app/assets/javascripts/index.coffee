
$( -> 
	$.ajaxSetup
		contentType: 'application/json; charset=UTF-8'
		timeout: 5000

	# メッセージ送信処理
	$('#sendMessage').click ->
		params =
			message: $('#message').val()
			image: $('#image').attr('src')

		$.post 'send', JSON.stringify(params), (d) ->
			console.log(d)
		, 'json'

	# ドラッグアンドドロップ処理
	addEventListener 'dragover', (ev) ->
		ev.preventDefault()
	,false

	addEventListener 'drop', (ev) ->
		ev.preventDefault()

		file = ev.dataTransfer.files[0]

		if file.type.indexOf('image/') is 0
			r = new FileReader()
			r.onload = (ev) -> $('#image').attr 'src', ev.target.result
			r.readAsDataURL file

	# WebSocket 処理
	ws = new WebSocket 'ws://localhost:9000/connect'
	ws.onmessage = (event) ->
		obj = JSON.parse event.data
		$('#list').prepend "<div><img class=\"chatimg\" src=\"#{obj.image}\" /><p class=\"msg\">#{obj.message}</p></div>"

	ws.onopen = (event) -> console.log "open : #{event}"
	ws.onclose = (event) -> console.log "close : code = #{event.code}, reason = #{event.reason}"

	$(window).bind 'beforeunload', ->
		console.log 'on before unload'
		ws.onclose = ->
		ws.close()
)
