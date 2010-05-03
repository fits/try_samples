
use Encode qw(from_to);
use Unicode::String;

print index("–{", "{") . "\n";

my $dataSrc = "–{";
my $dataTrg = "{";

my $uniSrc = new Unicode::String();
my $uniTrg = new Unicode::String();

from_to($dataSrc, "shiftjis", "utf8");
from_to($dataTrg, "shiftjis", "utf8");

$uniSrc->utf8($dataSrc);
$uniTrg->utf8($dataTrg);

print $uniSrc->index($uniTrg) . "\n";

