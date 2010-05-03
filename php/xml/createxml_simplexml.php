<?php

$xml = new SimpleXMLElement('<root />');

$xml->addChild('data', 'testdata');

echo $xml->asXML();
?>
