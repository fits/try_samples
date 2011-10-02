<?php

$url = $argv[1];
$user = $argv[2];
$pass = $argv[3];
$postData = $argv[4];

$options = array('http' => array(
	'method' => 'POST',
	'header' => 
		"Authorization: Basic " . base64_encode("$user:$pass") . "\r\n" . 
		"Content-Type: application/x-www-form-urlencoded\r\n",
	'content' => $postData
));

$res = file_get_contents($url, false, stream_context_create($options));

echo $res;

?>