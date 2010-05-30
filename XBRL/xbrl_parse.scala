import scala.xml.XML

val xml = XML.loadFile(args(0))

val values = xml \ "OperatingIncome" filter(n => (n \ "@contextRef").text == "CurrentYearConsolidatedDuration")

//OK
//val values = xml \ "OperatingIncome" filter(_.attribute("contextRef").mkString == "CurrentYearConsolidatedDuration")

//NG
//val values = xml \ "OperatingIncome" filter((_ \ "@contextRef").text == "CurrentYearConsolidatedDuration")

values.map(_.text).foreach(println)
