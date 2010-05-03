
print index("本", "{") . "\n";

open(F, 'data.txt');

while (<F>) {
	print index($_, "{") . "\n";
}

close(F);

print "include c\n" if ("田" =~ /c/);


#--------------- use encoding ---------
use encoding "shiftjis";

print index("本", "{") . "\n";

print "include c\n" if ("田" =~ /c/);

open(F, 'data.txt');

while (<F>) {
	print index($_, "{") . "\n";
}

close(F);
