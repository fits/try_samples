
sub createMultiCondition {
	local($columnName, @exprList) = @_;

	my $result = '';

	foreach $expr (@exprList) {
		$expr =~ s/[ ]*$//;

		if ($expr ne '') {
			$result .= " AND $columnName LIKE '%$expr%'";
		}
	}

	return $result;
}

$data = "USE TEA CH AB      JKL";

print createMultiCondition("ab", split(/ /, $data));

print "\n";

print createMultiCondition("ab", split(/ /, ''));


