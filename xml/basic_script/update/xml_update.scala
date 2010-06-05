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
			val ne = e % Attribute("", "type", "node", Null) % Attribute("", "ext", "updated", Null)
			//以下でも可
		//	e % new UnprefixedAttribute("type", "node", Null) % new UnprefixedAttribute("ext", "updated", Null)

			//子要素を処理
			val ch: NodeSeq = ne.child.zipWithIndex.map {l =>
				l._2 match {
					//要素を置換
					case 0 => <text>update test</text>
					//要素の値を変更
					case 1 => Elem(l._1.prefix, l._1.label, l._1.attributes, l._1.scope, Text("after"))
					case _ => l._1
				}
			}

			//子要素のみを変更した要素のコピーを作成
			ne.copy(child = ch)

		case n => n
	}

}

val newDoc = new RuleTransformer(rule).transform(Utility.trim(doc))

println(newDoc.mkString)
