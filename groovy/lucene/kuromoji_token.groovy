@Grab('org.apache.lucene:lucene-analyzers-kuromoji:8.11.0')
import org.apache.lucene.analysis.ja.JapaneseAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute

def analyzer = new JapaneseAnalyzer()

def st = analyzer.tokenStream(null, args[0])

st.reset()

while(st.incrementToken()) {
	println ''

	// 単語の取得
	def termAttr = st.getAttribute(CharTermAttribute)
	// 位置の取得
	def offsetAttr = st.getAttribute(OffsetAttribute)
	// 品詞の取得
	def posAttr = st.getAttribute(PartOfSpeechAttribute)

	println "term=${termAttr}, length=${termAttr.length()}, partOfSpeech=${posAttr.partOfSpeech}, startOffset=${offsetAttr.startOffset()}, endOffset=${offsetAttr.endOffset()}"
}

st.close()
