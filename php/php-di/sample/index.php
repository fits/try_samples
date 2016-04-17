<?php

require 'vendor/autoload.php';

$container = DI\ContainerBuilder::buildDevContainer();

$manager = $container->get('Abc\\SampleManager');

var_dump($manager);

echo $manager->callSample();
