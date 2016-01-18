
document.addEventListener('deviceready', function() {
	var map = plugin.google.maps.Map.getMap(
		document.getElementById('map')
	);

	map.addEventListener(plugin.google.maps.event.MAP_READY, function() {
		console.log('*** map ready');
	});

}, false);
