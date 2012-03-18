
$.getJSON "json", (json) -> 
	console.log(json)

	$('#data').append("<li>#{item.id} : #{item.name}</li>") for item in json

