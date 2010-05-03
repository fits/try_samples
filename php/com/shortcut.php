<?php
$wsh = new COM('WScript.Shell') or die;

$shortcut = $wsh->CreateShortcut(dirname(__FILE__) . '/test.lnk');
$shortcut->TargetPath = 'shortcut.php';
$shortcut->Save();

$wsh = null;

?>
