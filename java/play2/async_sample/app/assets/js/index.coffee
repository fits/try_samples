
$( -> $('#jobAdd').click ->
	$.get 'async?title=' + $('#title').val()
)
