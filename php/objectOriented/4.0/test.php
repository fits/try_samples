<?php

class Test {
	var $name;

	function Test() {
		$this->name = "";
	}

	function setName($name) {
		$this->name = $name;
	}

	function printName() {
		print "name: {$this->name}\n";
	}
}

$a =& new Test();
$b = $a;

$a->setName("test");
$a->printName();

$b->printName();


?>