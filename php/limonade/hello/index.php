<?php
require_once dirname(__FILE__) . '/lib/limonade.php';

dispatch('/', function() {
	return "hello world";
});

run();

?>
