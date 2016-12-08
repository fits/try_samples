<?php
require 'vendor/autoload.php';

$client = new Predis\Client();
//$client = new Predis\Client(['host' => 'localhost', 'port' => 6379]);

$client->set('data1', 123);
echo $client->get('data1');

$client->set('data2', 'abcdefg');
echo $client->get('data2');

?>