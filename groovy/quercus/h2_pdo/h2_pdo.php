<?php

$dbh = new PDO('jdbc:h2:db/testdb');

$sql = 'select id, name from customer';

foreach ($dbh->query($sql) as $r) {
	echo "{$r[0]}\t{$r[1]}\n";
}

?>