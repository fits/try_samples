<?php

echo(shell_exec("test.bat"));

$output;
$ret;

echo(exec("test.bat", $output, $ret));

echo("-----------");
echo("output=$output\n");
echo("status=$ret\n");

?>
