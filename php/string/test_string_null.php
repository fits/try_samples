<?php

$st = "";
//$st = null;

if ($st != null) {
	echo "$st is not null (!=)";
}
else {
	echo "$st is null (!=)";
}

echo ", ";

if ($st !== null) {
	echo "$st is not null (!==)";
}
else {
	echo "$st is null (!==)";
}


?>