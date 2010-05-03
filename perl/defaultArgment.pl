
sub test{
	local($str, $num) = @_;

	if ($num < 1) {
		$num = 1;
	}

	print substr($str, 0, $num)."\n";
}

test("12345");

test("12345", 3);

