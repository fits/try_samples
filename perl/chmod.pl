
open(DATA, "> test.txt");

print DATA <<"EOD";
a
b
c
EOD
;

close(DATA);

chmod 0755, "test.txt";
