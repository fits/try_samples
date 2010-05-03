<?php

$data = "\t123\t4\t";

$list = split("\t", $data);

var_dump($list);


list($a, $b, $c, $d) = split("\t", $data);

var_dump($a);
var_dump($b);
var_dump($c);
var_dump($d);

?>
