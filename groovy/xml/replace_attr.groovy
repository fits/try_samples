/**
* 子の属性の値を親の属性に設定するサンプル
*/

import groovy.xml.StreamingMarkupBuilder

def doc = new XmlSlurper().parse(new File(args[0]))

doc.sub.each {
	if (it.@category == "") {

		def item = it.children().find {ch -> ch.name() == "item"}
		it.@category = item.@category.text()

	}
}

def builder = new StreamingMarkupBuilder()

println builder.bind{
	mkp.yield doc
}
