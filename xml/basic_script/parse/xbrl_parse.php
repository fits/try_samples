<?php

$doc = simplexml_load_file($argv[1]);

$values = $doc->xpath("//jpfr-t-cte:OperatingIncome[@contextRef='CurrentYearConsolidatedDuration']");

foreach($values as $v) {
	echo "$v\n";
}

