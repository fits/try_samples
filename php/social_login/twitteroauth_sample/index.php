<?php
	require_once('config.php');
	require_once('twitteroauth/twitteroauth.php');

	$con = new TwitterOAuth($conf['key'], $conf['secret']);

	$token = $con->getRequestToken('http://127.0.0.1:8080/callback.php');
//	$token = $con->getRequestToken();

	$url = $con->getAuthorizeURL($token);

	header("Location: {$url}");
?>
