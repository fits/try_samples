<?php
require_once dirname(__FILE__) . '/lib/limonade.php';

function configure() {
	option('env', ENV_DEVELOPMENT);
	option('debug', true);
}

dispatch('/', function() {
	return "hello";
});

dispatch('/:name/:value', function() {
	$name = params("name");
	$value = params("value");

	return "sample page name: $name, value: $value";
});

run();

?>
