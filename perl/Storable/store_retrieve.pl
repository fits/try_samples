
use Storable qw(store retrieve);
use Data::Dumper;

$list = {};

$list->{'ABC'} = ['テスト1', '表1', 'チェック'];
$list->{'123'} = ['データ'];
$list->{'test'} = [];

store($list, 'data.dat');

$loadList = retrieve('data.dat');

print Data::Dumper->Dumper($loadList);

print "\n---" . ($loadList->{'ABC'}[1]) . "---\n";


while(($key, @value) = each %$loadList) {
	print "==== $key = @value \n";
}
