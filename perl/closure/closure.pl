
use strict;

sub process {
	my ($processor) = @_;

	my @list = split(/\,/, "a,b,c,d");

	foreach(@list) {
		$processor->($_);
		print "item : $_\n";
	}
}

sub count {
	my $counter = 0;

	my $proc = sub {
		my ($item) = @_;

		$counter++;
	};

	&process($proc);

	print "count : $counter\n";
}

sub first {
	my $res;

	my $proc = sub {
		my ($item) = @_;

		$res = $item;
		last;
	};

	&process($proc);

	print "first item : $res\n";
}

count();

first();