use Net::SMTP;
use Encode qw(from_to);

$host = 'localhost';
$from = 'fits@localhost';
$to = 'fits@localhost';

$smtp = Net::SMTP->new($host, Hello => $host, Debug => 1);

$smtp->mail($from);
$smtp->to($to);

$smtp->data();
$smtp->datasend("From: $from\n");
$smtp->datasend("To: $to\n");
$smtp->datasend("Subject: SendMail Test from Perl\n");
$smtp->datasend("Content-Type: text/html; charset=\"iso-2022-jp\"\n");
$smtp->datasend("Content-transfer-encoding: 7bit\n");
$smtp->datasend("\n");

$content =<< "EOD";
<html>
<head>
<title>send mail test</title>
</head>
<body>
<h1>テストメール from Perl</h1>
<h2>リスト</h2>
<ul>
  <li>ABC</li>
  <li>DEF</li>
</ul>
</body>
</html>
EOD

from_to($content, "shiftjis", "jis");
$smtp->datasend($content);

$smtp->datasend();
$smtp->quit();

