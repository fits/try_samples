<?php

$doc = new DOMDocument();

if ($doc->load("test.xml")) {

	$xp = new DOMXPath($doc);
	$node = $xp->query("//item");

	$res = $node->item(0);

	if ($res) {
		echo "result : " . trim($res->nodeValue);
	}
}

?>
