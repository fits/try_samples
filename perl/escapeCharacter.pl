$a = "c:\test\dbbc'123";

print $a."\n";

$a =~ s/\\/\\\\/g;

print $a."\n";

$a =~ s/'/\\'/g;

print $a."\n";

