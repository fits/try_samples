<?php

$list = array("a", "b", "c");

$ret = array_search("-a", $list);

if ($ret === False) {
	echo "failed false";
}
else {
	echo "success $ret";
}

?>
