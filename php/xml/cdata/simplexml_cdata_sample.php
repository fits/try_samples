<?php

$xml = "<data><![CDATA[ test & <data> ]]></data>";

$d = simplexml_load_string($xml);
//CDATA セクションの内容がパースされない
var_dump($d);

$d = simplexml_load_string($xml, 'SimpleXMLElement', LIBXML_NOCDATA);
//CDATA セクションの内容がパースされる
var_dump($d);



/* 出力結果

object(SimpleXMLElement)#1 (0) {
}
object(SimpleXMLElement)#2 (1) {
  [0]=>
  string(15) " test & <data> "
}

*/

