<?php

class SimpleXMLElementExt extends SimpleXMLElement {
	public function addChild($name, $value = null, $namespace = null) {
		echo "call ext addChild\n";

		if ($value) {
			$value = str_replace('&', '&amp;', $value);
		}

		return parent::addChild($name, $value, $namespace);
	}
}

function createSimpleXMLElement($data) {
	return new SimpleXMLElementExt($data);
}


$xml = createSimpleXMLElement('<root />');

$xml->addChild('data', 'test < &amp; " % , ; : +-?^~\ 123');
$xml->addChild('data2', 'test & tset');
$ch = $xml->addChild('data3');

$ch->addChild('child1', '1 & 2 & 3');

$ch2 = $ch->addChild('child-child1');
$ch2->addChild('a')->addChild('b', 'test &&& test');

echo $xml->asXML();
?>
