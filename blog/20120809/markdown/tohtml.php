<?php
include_once "markdown.php";

$mkStr = stream_get_contents(STDIN);

echo Markdown($mkStr);
?>