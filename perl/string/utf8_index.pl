
use Encode qw(from_to);

print index("AB田本", "c") . "\n";

my $dataSrc = "AB田本";
my $dataTrg = "B田";

from_to($dataSrc, "shiftjis", "utf8");
from_to($dataTrg, "shiftjis", "utf8");

#$dataSrc = uc($dataSrc);
#$dataTrg = uc($dataTrg);

print index($dataSrc, $dataTrg) . "\n";

print ($dataSrc =~ /$dataTrg/) . "\n";
