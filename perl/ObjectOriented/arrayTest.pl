require "Data.pm";

my @list = ();

my $d1 = Data->new("test1", 100);
push(@list, $d1);

my $d2 = Data->new("test2", 5);
push(@list, $d2);

print "count : " . ($#list + 1) . "\n";

foreach(@list) {
	my $name = $_->getName();
	my $point = $_->getPoint();

	print "name: $name, point: $point\n";
}

my @hashList = ();
my %attr1 = {};
$attr1{"name"} = "test1";
$attr1{"point"} = 100;
push(@hashList, %attr1);

my $attr2 = {
	"name" => "test2",
	"point" => 5
};
push(@hashList, $attr2);

print "count : " . ($#hashList + 1) . "\n";

foreach(@hashList) {
	my $name = $_->{"name"};
	my $point = $_->{"point"};

	print "name: $name, point: $point, $_\n";
}
