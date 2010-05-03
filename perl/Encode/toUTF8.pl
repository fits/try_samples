
#use Encode;
use Encode qw(from_to);

$data = "テストデータ";

#Encode::from_to($data, "shiftjis", "utf8");
from_to($data, "shiftjis", "utf8");

print $data;
