
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.tools.Compiler;

class GroovySimpleCompiler {
	public static void main(String[] args) {

		CompilerConfiguration conf = new CompilerConfiguration();
		//出力先ディレクトリを設定
		conf.setTargetDirectory("dest");

		Compiler compiler = new Compiler(conf);

		//実行時引数で指定した Groovy スクリプトファイルをコンパイル
		compiler.compile(args);
	}
}
