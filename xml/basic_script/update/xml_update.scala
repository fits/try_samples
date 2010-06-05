import scala.xml._
import scala.xml.transform.{RewriteRule, RuleTransformer}

val doc = XML.loadFile(args(0))

val rule = new RewriteRule {
	override def transform(n: Node): NodeSeq = n match {
		//要素の追加
		case <root>{ ch @ _* }</root> => <root>{ ch }<data id="3"><details>added</details></data></root>

		//要素の削除
		case e: Elem if (e \ "@id").text == "1" => NodeSeq.Empty

		//属性の追加と変更
		case e: Elem if (e \ "@id").text == "2" =>
			e % Attribute("", "type", "node", Null) % Attribute("", "ext", "updated", Null)
			//以下でも可
		//	e % new UnprefixedAttribute("type", "node", Null) % new UnprefixedAttribute("ext", "updated", Null)

		

		case n => n
	}
}

val newDoc = new RuleTransformer(rule).transform(doc)

println(newDoc.mkString)
