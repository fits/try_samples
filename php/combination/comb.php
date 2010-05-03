<?php

$list = array(1, 2, 3, 4, 5);

for ($i = 0; $i < count($list); $i++) {
	for ($j = $i; $j < count($list); $j++) {
		echo($list[$i] . " - " . $list[$j] . "\n");
	}
}

?>
