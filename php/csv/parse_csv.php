<?php

if (($h = fopen($argv[1], "r")) !== FALSE) {
	while (($r = fgetcsv($h)) !== FALSE) {
		echo "$r[0] : $r[2]\n";
	}
	fclose($h);
}
