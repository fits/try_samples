<?php

$xml = new SimpleXMLElement('<root />');

$xml->addChild('data', 'test < &amp; " % , ; : +-?^~\ 123');
$xml->addChild('data2', 'test & tset');

echo $xml->asXML();
?>
