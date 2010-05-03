
use DBI;

use DBD::DB2::Constants;
use DBD::DB2;

$dbh = DBI->connect("dbi:DB2:sample", "db2test", "db2testpass");

$ontime = times();

my $sth = $dbh->prepare("SELECT * FROM demo.employee ORDER BY empno") || die $dbh->errstr;

$sth->execute() || die $sth->errstr;

while (my($no, $firstName, $midinit, $lastName, $workdept, $tel, $hireDate) = $sth->fetchrow_array) {
	print "$no, $firstName, $hireDate\n";
}

print "time: " . (times() - $ontime) . "s\n";


print "------------ fetch 0-15 -------------\n";

$ontime = times();

$sth = $dbh->prepare("SELECT * FROM demo.employee ORDER BY empno  FETCH FIRST 15 ROWS ONLY") || die $dbh->errstr;

$sth->execute() || die $sth->errstr;

while (my($no, $firstName) = $sth->fetchrow_array) {
	print "$no, $firstName\n";
}

print "time: " . (times() - $ontime) . "s\n";


print "------------ fetch 10-25 -------------\n";

$ontime = times();

$select =<< "EOD";
SELECT * FROM demo.employee
  EXCEPT(
     SELECT * FROM demo.employee ORDER BY empno
     FETCH FIRST 10 ROWS ONLY
  )
ORDER BY empno
FETCH FIRST 15 ROWS ONLY
EOD
;

$sth = $dbh->prepare($select) || die $dbh->errstr;

$sth->execute() || die $sth->errstr;

while (my($no, $firstName) = $sth->fetchrow_array) {
	print "$no, $firstName\n";
}

print "time: " . (times() - $ontime) . "s\n";

$dbh->disconnect;
