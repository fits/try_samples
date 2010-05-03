<?php
require_once 'PHPUnit/Framework.php';
require_once 'Counter.php';

class CounterTest extends PHPUnit_Framework_TestCase {

	private $counter;

	function setup() {
		$this->counter = new Counter();
	}

	function tearDown() {
	}

	function testCountUp() {
		$this->assertEquals(1, $this->counter->countUp());
	}

	/**
	* @test
	*/
	function countUpTest() {
		print "countup-test";
	}

	function testMultiCountUp() {
		$this->assertEquals(1, $this->counter->countUp());
		$this->assertEquals(2, $this->counter->countUp());
	}

	function testGetCount() {
		$this->assertEquals(0, $this->counter->getCount());

		$this->counter->countUp();

		$this->assertEquals(1, $this->counter->getCount());
	}

	function testCountUpWithNumber() {
		$this->assertEquals(10, $this->counter->countUp(10));
	}

	function testCountUpWithZeroNumber() {
		try {
			$this->counter->countUp(0);
		}
		catch (InvalidArgumentException $ex) {
			return;
		}

		$this->fail();
	}

	/**
	* @test
	* @expectedException InvalidArgumentException
	*/
	function countUpWithZeroNumberTest() {
		$this->counter->countUp(0);
	}


}
?>
