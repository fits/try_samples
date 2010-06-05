<?php

$doc = simplexml_load_file($argv[1]);

//要素の追加
$d = $doc->addChild("data");
$d["id"] = "3";
$d->addChild("details", "added");

//属性の追加
$doc->data[1]["type"] = "node";

//要素の削除
unset($doc->data[0]);

//要素の置換（DOMDocument の API を使用）
$dom = dom_import_simplexml($doc);
$target = $dom->getElementsByTagName("details")->item(0);
$target->parentNode->replaceChild(new DOMElement("text", "update test"), $target);
$doc = simplexml_import_dom($dom);

//要素の値を変更
$doc->data[0]->details[0] = "after";

//属性の変更
$doc->data[0]["ext"] = "updated";

echo $doc->asXML();

