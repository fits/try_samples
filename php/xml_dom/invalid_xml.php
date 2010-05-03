<?php

$doc = new DOMDocument();

$ret = $doc->load("invalid.xml");

if ($ret) {
	echo "result : ${ret} \n";
}
else {
	echo "failed \n";
}

?>
