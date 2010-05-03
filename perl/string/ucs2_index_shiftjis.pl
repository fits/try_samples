
use Encode qw(from_to);

print index("AB田本", "c") . "\n";

my $dataSrc = "AB田本";
my $dataTrg = "c本";

from_to($dataSrc, "shiftjis", "ucs2");
from_to($dataTrg, "shiftjis", "ucs2");

my $pos = 0;
my $result = -1;

while (($pos = index($dataSrc, $dataTrg, $pos)) >= 0) {
	if (($pos % 2) eq 0) {
		$result = $pos;
		last;
	}
	else {
		$pos++;
	}
}

print $result;

