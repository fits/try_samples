<?php

$file = "filesize.php";

//SplFileInfo クラスの利用
$f = new SplFileInfo($file);

$fs = sprintf("%u", $f->getSize());

echo "filesize : " . $fs . "\n";
echo "date : " . date("YmdHis", $f->getMTime()) . "\n";


//各種関数の利用
//4GB までのファイルサイズを取得できるようにする
$fs = sprintf("%u", filesize($file));

echo "filesize : $fs \n";
echo "date : " . date("YmdHis", filemtime($file)) . "\n";

?>
