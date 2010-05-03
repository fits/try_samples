<?php

function printtime($encode) {
	//setlocale に失敗すると前回の値が使用されるため
	//C でクリアする
	setlocale(LC_TIME, 'C');
	$result = setlocale(LC_TIME, $encode);

	echo strftime("%B - %A - %a")." ($encode) - $result \r\n";
}

printtime('ja_JP');

printtime('ja_JP.UTF-8');

printtime('ja');

printtime('Japanese_Japan.932');

printtime('CP932');

printtime('UTF-8');

printtime('Japanese_Japan.65001');

?>