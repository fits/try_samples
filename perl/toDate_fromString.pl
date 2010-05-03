
use Time::Local;

sub printDate {
	my ($dateString) = @_;

	if ($dateString =~ /(\d+)\/(\d+)\/(\d+) (\d+):(\d+):(\d+)/) {
		print "year: $1\n";
		print "month: $2\n";
		print "day: $3\n";
		print "hour: $4\n";
		print "minute: $5\n";
		print "second: $6\n";

		my $time = timelocal($6, $5, $4, $3, $2, $1);
		print "time : $time\n";
	}
	else {
		print "invalid date: $dateString";
	}
}

printDate("2009/02/20 12:00:01");

print "\n";

printDate("2009/2/20 9:00:01");

