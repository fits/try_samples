<?php
$imgRoot = '/var/img';

$path = explode('?', $_SERVER['REQUEST_URI'])[0];
$width = intval($_GET['width']);

$file = $imgRoot . $path;

if (file_exists($file)) {
	$im = new Imagick($file);
	$type = strtolower($im->getImageFormat());

	header("Content-Type: image/${type}");

	if ($width > 0) {
		// サムネイル化
		$im->thumbnailImage($width, 0);
	}
	echo $im;
}
else {
	header("HTTP/1.0 404 Not Found");
}
?>
