<?php

$string = <<<XML
<root>
	<item code="C1" event="E1" title="part1" />
	<item code="D1" event="E2" title="part2" />
</root>
XML;

$xml = simplexml_load_string($string);

var_dump($xml);

print json_encode($xml);

$list = array("root" => array());

echo "\n";

foreach ($xml->item as $it) {
	echo $it['code'] . "\n";
}

?>
