
sub fileCopy {
	local($src, $trg) = @_;

	open IN, "$src";
	open OUT, "> $trg";

	binmode(IN);
	binmode(OUT);

	while (<IN>) {
		print OUT $_;
	}

	close OUT;
	close IN;
}

fileCopy("a.ppt", "b.ppt");
