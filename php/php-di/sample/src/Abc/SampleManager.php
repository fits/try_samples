<?php

namespace Abc;

class SampleManager {
	private $sample;

	public function __construct(Sample $sample) {
		$this->sample = $sample;
	}

	public function callSample() {
		return $this->sample->call();
	}
}
