<?php

//$str = '{"result":"true", "registno":"10"}';

$str = file_get_contents("http://127.0.0.1/test.php");

echo("$str\n");

$r = json_decode($str, true);

var_dump($r);

?>