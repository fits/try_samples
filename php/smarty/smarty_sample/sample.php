<?php
	require_once('Smarty/Smarty.class.php');

	$smarty = new Smarty;
	/* 
	 * カレントの plugins ディレクトリから
	 * Smarty プラグインを参照するための設定※
	 *
	 * ※ デフォルトは lib/Smarty/plugins になる
	 *
	 * なお、以下のように設定すると
	 * $smarty->plugins_dir[] = 'plugins';
	 * 
	 * indirect modification of overloaded property has no effect が
	 * 出力されるので注意
	 */
	$smarty->plugins_dir = array('plugins');

	$smarty->assign('name', 'test data');

	$smarty->display('sample.tpl');

?>