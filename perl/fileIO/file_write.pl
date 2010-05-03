
$ret = open(SQL, "> temp/sqlfile.txt");

if ($ret == 1) {

	print SQL <<EOD;
aaa
bbb
ccc
EOD
;

	close(SQL);
}
else {
	print "file open fail !!!";
}
