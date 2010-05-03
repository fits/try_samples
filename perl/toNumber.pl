
$num = hex("FF");

print $num . "\n"; #255

$num = $num + 5;

print $num . "\n"; #260

print ($num + "3aaa1") . "\n"; #263

$data = "abcdefg.";

chop($data);

print "$data\n";

chop($data);

print "$data\n";


$data = "    123 44a    ";

$data =~ s/^\s+//;
$data =~ s/\s+$//;

print $data . "|\n";

sub test {
	return wantarray ? ("abc", "def", "ghi") : "a";
}

@data = test();

print @data;
print "\n";

$data1 = test();

print $data1;

