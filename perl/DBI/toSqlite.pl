
use DBI;

my $dbh = DBI->connect("dbi:SQLite:dbname=test.db", "", "");

#$dbh->do("CREATE TABLE test(id, name, point)");

my $insert = $dbh->prepare("INSERT INTO test VALUES(?, ?, ?)");
$insert->execute(10, "testdata", 100);
$insert->execute(20, "aaa", 1);

my $select = $dbh->prepare("SELECT * FROM test WHERE name like ?") || die $dbh->errstr;

#execute にパラメータを指定する事も可
#$select->execute("%a%") || die $select->errstr;

#プレースホルダのインデックスは 1 から
$select->bind_param(1, "%da%");
$select->execute() || die $select->errstr;

#下記では検索件数は取得できない
print "count = " . $select->rows . "\n";

while (my($id, $name, $point) = $select->fetchrow_array) {
	print "$id, $name, $point\n";
}


#以下でも可
#while (my $rec = $select->fetch) {
#	print "$rec->[0], $rec->[1], $rec->[2]\n";
#}


#SQLite の場合は以下は不要
#$rc = $dbh->disconnect or warn $dbh->errstr;

