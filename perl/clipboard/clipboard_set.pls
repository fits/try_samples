use Win32::Clipboard;

my $clip = Win32::Clipboard();

$clip->Empty();

$clip->Set("time:" . time);

