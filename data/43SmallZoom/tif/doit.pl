#!/usr/bin/perl -w

use strict;

my @files = glob('*.tif');

foreach my $file (@files){
	next unless $file =~ /(.+)\.(tif)/;
	system("convert $file $1.jpg");
}
