<?php
class DbTestShell extends Shell {
	var $uses = array('User');

	function main() {
		$rows = $this->User->findAll();

		foreach ($rows as $row) {
			echo "user: " . $row['User']['name'] . "\n";
		}

	}
}
?>
