<?php
class SampleRunShell extends Shell {
	var $tasks = array('Sample');

	function main() {

		if (sizeof($this->args) > 0) {
			print "args: " . $this->args[0] . "\n";
		}

		$this->Sample->test();
	}
}
?>
