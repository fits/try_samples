<?php

function appendChildForSimpleXML($parent, $child) {
	$parentNode = dom_import_simplexml($parent);
	$childXml = dom_import_simplexml($child);
	$childNode = $parentNode->ownerDocument->importNode($childXml, true);

	return simplexml_import_dom($parentNode->appendChild($childNode));
}

$xml = new SimpleXMLElement('<root />');

$xml->addChild('data', 'test');
$parent = $xml->addChild('data', 'aaa');


$node = new SimpleXMLElement('<child>123</child>');

appendChildForSimpleXML($parent, $node);

echo $xml->asXML();

unset($xml->data[0]);

echo $xml->asXML();

foreach ($xml->xpath("//child") as $ch) {
	echo "--- $ch";
	$ch["id"] = "aaaabbbbcccc";
	$ch[0] = "a1b2c3" . $ch[0];
	echo "$ch";
}

//$xml->data->child = "a1b2";

echo $xml->asXML();


?>
