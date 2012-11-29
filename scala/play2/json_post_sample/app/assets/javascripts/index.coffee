
$( -> 
	$.ajaxSetup
		contentType: 'application/json; charset=UTF-8'
		timeout: 5000

	# メッセージ送信処理
	$('#sendMessage').click ->
		params =
			message: $('#message').val()

		url = "send?" + $('#query').val()

		$.post url, JSON.stringify(params), (d) ->
			console.log(d)
		, 'json'
)
