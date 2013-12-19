@Grab("org.jsoup:jsoup:1.7.3")
import org.jsoup.Jsoup

def html = '''
<html>
<body>
	<div>
		<img src="sample.png" width="1" height="1">
		<img src="sample2.png" width="1" height="10">
	</div>
	<div>
		<a href="a.html">
			<span value="a">テスト</span>
		</a>
		<a href="b.html">
			<span value="abc">テストbデータ</span>
		</a>
	</div>
</body>
</html>
'''

def dump = { doc, selector ->
	println "----- ${selector} -----"
	println doc.select(selector).dump()
}

def doc = Jsoup.parse(html)

dump doc, 'img[width=1]'

dump doc, 'img[width=1][height=1]'

dump doc, 'a:has(span[value*=b])'

dump doc, 'a:has(span:contains(データ))'

