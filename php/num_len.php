<?php

$status = 12345;

	function setRerunFlag($status) {
		$result = $status;

		if (strlen($status) >= 5) {
			$result = subStr($status, 0, 3) . '1' . subStr($status, 4, 1);
		}

		return $result;
	}

echo "len: " . strlen($status);

echo "rerun : " . setRerunFlag($status);


?>