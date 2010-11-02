<?php
//言語設定（UTF-8/Base64）
mb_language("uni");
//内部文字コード
mb_internal_encoding("UTF-8");

if (count($argv) - 1 != 4) {
	print("<SMTPServer> <From> <To> <Subject>\n");
	return;
}

//文字コードを UTF-8 に変換する
function to_utf8($str) {
	return mb_convert_encoding($str, "UTF-8", "SJIS");
}

//Windows 環境用の設定
ini_set("SMTP", $argv[1]);

$subject = mb_encode_mimeheader(to_utf8($argv[4]));

$body = to_utf8(stream_get_contents(STDIN));

mb_send_mail($argv[3], $subject, $body, "From: $argv[2]");

//print("$argv[4] = $subject\n");
//print("$body\n");

?>