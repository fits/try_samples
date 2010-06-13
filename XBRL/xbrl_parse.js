
var doc = WScript.CreateObject("MSXML2.DOMDocument");

doc.async = false;
doc.setProperty("SelectionLanguage", "XPath");
//XMLñºëOãÛä‘ÇÃê›íË
doc.setProperty("SelectionNamespaces", "xmlns:jpfr-t-cte='http://info.edinet-fsa.go.jp/jp/fr/gaap/t/cte/2010-03-11'");

doc.load(WScript.Arguments.Item(0));

var values = doc.selectNodes("//jpfr-t-cte:OperatingIncome[@contextRef='CurrentYearConsolidatedDuration']");

for (var i = 0; i < values.length; i++) {
	WScript.Echo(values.item(i).text);
}
