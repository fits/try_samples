
use encoding "shiftjis";

$_ = "{“c –½Š}‹U}";

my @temp = /\{([^}]*)\}/g;

print $temp[0];

