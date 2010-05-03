
$data = "テストデータ";

sub toUtf8 {
	local($str) = @_;
	local($result) = "";

	open(PSLIST, "echo $str | nkf -Sw8u |") or die "Can't open pipe";

    while(<PSLIST>) {
		$result .= $_;
    }

	close(PSLIST);

	return $result;
}

print toUtf8($data);

