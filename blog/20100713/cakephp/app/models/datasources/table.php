<?php

class Table extends AppModel {

	//Table Schema の一覧取得
	function findAllSchemas() {
		$list = $this->find("all", array(
			"fields" => "DISTINCT table_schema", 
			"order" => "table_schema"
		));

		return $this->toArray($list);
	}

	//指定の Table Schema に所属する Table を取得
	function findTables($tableSchema) {
		$list = $this->find("all", array(
			"conditions" => array(
				"table_schema" => $tableSchema
			),
			"fields" => "table_name, table_type, engine, avg_row_length, create_time"
		));

		return $this->toArray($list);
	}

	/** 
	 * モデル名に対する連想配列の配列を配列の配列に変換する
	 *
	 * <変換前>
	 *  array(
	 *    array("Table" => array("table_schema" => ・・・)),
	 *    array("Table" => array("table_schema" => ・・・)),
	 *    ・・・
	 *  )
	 *
	 * <変換後>
	 *  array(
	 *    array("table_schema" => ・・・),
	 *    array("table_schema" => ・・・),
	 *    ・・・
	 *  )
	 */
	private function toArray($findResult) {
		$outerThis = $this;

		return array_map(function($l) use ($outerThis) {
			return $l[$outerThis->name];
		}, $findResult);
	}
}
