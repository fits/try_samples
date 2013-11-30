<?php
$dbh = new PDO('oci:dbname=//localhost:1521/XE', 'U1', 'P1');

$sql = 'select ID, NAME from CUSTOMER';

foreach ($dbh->query($sql) as $r) {
	print "{$r['ID']}\t{$r['NAME']}\n";
}

?>