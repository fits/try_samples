<?php

class Counter {

	private $count = 0;

	/*
	function countUp() {
		$this->count++;
		return $this->count;
	}
	*/

	function countUp($num = 1) {
		if ($num == 0) {
			throw new InvalidArgumentException;
		}

		$this->count += $num;
		return $this->count;
	}

	function getCount() {
		return $this->count;
	}
}
?>
