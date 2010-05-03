use Win32::Clipboard;

my $clip = Win32::Clipboard();

$clip->Set("test data");

print $clip->GetText();

