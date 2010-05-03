<?php

function updateList($list) {
	$list[0] = 1;
}

function updateList2(&$list) {
	$list[0] = 1;
}

$val = array(0, 2);

updateList($val);

print "$val[0]\n";

updateList2($val);

print "$val[0]\n";
