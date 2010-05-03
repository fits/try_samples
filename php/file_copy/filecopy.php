<?php

$destFile = ".\\data\\sample\\filecopy.php";

if (!file_exists(dirname($destFile))) {
	mkdir(dirname($destFile), 0777, true);
}

copy("filecopy.php", $destFile);

if ($d = opendir(dirname($destFile))) {
	while (false !== ($file = readdir($d))) {
		if (!is_dir($file)) {
			echo $file . "\n";
		}
	}

	closedir($d);
}

unlink($destFile);

rmdir(".\\data");

?>