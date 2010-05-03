<?php

$date = new DateTime('2009/06/08 14:01:03');

echo $date->format('Y - m - d HŽž i•ª s•b');

$date2 = new DateTime();
echo $date2->format('Ymd') . "\n";

echo sprintf("%04d", 6) . "\n";



$now = new DateTime();
$date3 = $now->format('Ymd');

$todayMaxReqNo = '20090610_9999';

$dateList = split('_', $todayMaxReqNo);

$no = $dateList[1] + 1;
echo $date3 . '_' . sprintf("%04d", $no);

?>