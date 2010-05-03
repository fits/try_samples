#!/usr/bin/perl
use strict;

my $max = 10;

#setpgrp or die;

my $pid;

for (my $i = 0; $i < $max; $i++) {
	defined($pid = fork) or die;
	last unless $pid;
}

if ($pid) {
	sleep 30;
	exit;
}

print "fork $$ \n";
