<?php

$file = $argv[1];
$width = intval($argv[2]);

if ($width > 0 && file_exists($file)) {
	$im = new Imagick($file);

	$type = strtolower($im->getImageFormat());

	// サムネイル作成
	$im->thumbnailImage($width, 0);

	$paths = pathinfo($file);

	// ファイルへ保存
	$im->writeImage($paths['filename'] . "_${width}.${type}");
}

?>