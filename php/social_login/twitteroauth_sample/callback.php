<?php
	require_once('config.php');

	if (isset($_GET['error'])) {
		header('Location: index.php');
		exit();
	}

	$token = $_GET['oauth_token'];

	echo $token;

?>
