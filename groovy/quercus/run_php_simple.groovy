import com.caucho.quercus.QuercusEngine

def qc = new QuercusEngine()
def output = new ByteArrayOutputStream()

output.withStream {
	// 出力先を設定（デフォルトでは標準出力される）
	qc.outputStream = it
	// PHP スクリプトの実行
	def res = qc.executeFile(args[0])

	println "res = $res"
}

println "-----"
// 結果の出力
println output.toString('UTF-8')
