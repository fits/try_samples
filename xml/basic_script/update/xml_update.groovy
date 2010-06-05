
import groovy.xml.StreamingMarkupBuilder

def doc = new XmlSlurper().parse(new File(args[0]))

//要素の追加 <data id="3"><details>added</details></data>
doc.appendNode {
	data(id: "3") {
		details("added")
	}
}

//属性の追加 <data id="2"> に type="node" を追加
doc.data.find {it.@id == "2"}.@type = "node"
//以下でも可
doc.data[1].@type = "node"

//以下は不可。appendNode した要素は find や配列でアクセスできない模様
//doc.data.find {it.@id == "3"}.@type = "node1"
//doc.data[2].@type = "node2"


//要素の削除 <data id="1">・・・</data> を削除
doc.data.find {it.@id == "1"}.replaceNode {}


//要素の変更
doc.data.find {it.@id == "2"}.children()[0].replaceNode {
	text("update test")
}
//以下でも可
/*
doc.data[1].details[0].replaceNode {
	text("update test")
}
*/

//要素の値を変更
doc.data.find {it.@id == "2"}.details[1] = "after"

//属性の変更 <data id="2" ext="none"> を ext="updated" に変更
doc.data.find {it.@id == "2"}.@ext = "updated"


//文字列化して出力
println new StreamingMarkupBuilder().bind{
	mkp.yield doc
}
