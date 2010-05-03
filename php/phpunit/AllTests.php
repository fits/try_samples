<?php
require_once 'PHPUnit/Framework/TestSuite.php';

class AllTests
{
    public static function suite()
    {
        $suite = new PHPUnit_Framework_TestSuite();

        include_once 'ArrayTest.php';
        $suite->addTestSuite('ArrayTest');

        return $suite;
    }
}