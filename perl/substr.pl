$vid = "abcdfg3a";

$num = substr($vid, 6, 1);

print int($num)."\n";

$num2 = substr($vid, 5, 1);

print int($num2);

$num2 = substr($vid, 35, 1);

print int($num2);

if ($num =~ /[0-9]/) {
	print "\n $num is number";
}
else {
	print "not number";
}
