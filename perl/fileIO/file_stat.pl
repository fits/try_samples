
my @res = stat("file_stat.pl");

my $uid = $res[4];
my $size = $res[7];
my $updateDate = $res[9];

print "user: $uid, size: $size, update: $updateDate\n";

my ($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) = localtime($updateDate);

my $dateString = sprintf("%d/%02d/%02d %02d:%02d:%02d", ($year + 1900), ($mon + 1), $mday, $hour, $min, $sec);

print $dateString;
