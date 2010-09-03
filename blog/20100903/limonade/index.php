<?php
require_once dirname(__FILE__) . '/lib/limonade.php';

//ルート / へのアクセス時の処理
dispatch('/', function() {
	return "hello";
});

// /name/value へのアクセス時の処理
dispatch('/:name/:value', function() {
	$name = params("name");
	$value = params("value");

	return "sample page name: $name, value: $value";
});

run();

?>
