<?php

$arrayList = array(1, 2, 3);
$hashList = array("id" => "1", "name" => "test");

print "array data : $arrayList[0]\n";
print "hash data : {$hashList['name']}\n";

$arrayList[3] = 100;
$hashList["check"] = "ok";

print "array data : $arrayList[3]\n";
print "hash data : {$hashList['check']}\n";

array_push($arrayList, 12);
print "array data : $arrayList[4]\n";

$arrayList[] = 13;
print "array data : $arrayList[5]\n";


?>