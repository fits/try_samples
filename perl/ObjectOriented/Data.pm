
use strict;

package Data;

sub new {
	my ($class, $name, $point) = @_;

	my $self = {
		name => $name,
		point => $point
	};

	bless $self, $class;
	return $self;
}

sub getName {
	my ($self) = @_;

	return $self->{name};
}

sub getPoint {
	my ($self) = @_;

	return $self->{point};
}

1;
