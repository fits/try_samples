import java.io.File
import scala.io.Source

object FileReadTest {
	def main(args: Array[String]) {

		//Scala 2.8 用の File 読み込み処理
		if (args.length < 1) {
			println("scala read_file.scala [filename]")
			exit
		}


		Source.fromFile(new File(args(0))).getLines().foreach(println)
	}
}
