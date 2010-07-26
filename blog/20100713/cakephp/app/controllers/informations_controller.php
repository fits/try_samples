<?php
class InformationsController extends AppController {

	//使用するモデルの定義
	var $uses = "Table";
	//View を使わないようにするための設定
	var $autoRender = false;

	//Table Schema の一覧を JSON で返す
	function databases() {
		$list = $this->Table->findAllSchemas();
		return json_encode($list);
	}

	//指定 Table Schema に所属する Table の一覧を JSON で返す
	function tables($tableSchema) {
		$list = $this->Table->findTables($tableSchema);
		return json_encode($list);
	}
}
