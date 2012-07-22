package fits.sample

import scala.io.Source
import com.tristanhunt.knockoff.DefaultDiscounter._

object Sample {
	def main(args: Array[String]) {
		val markdown = Source.fromFile(args(0)).mkString

		val html = toXHTML(knockoff(markdown))

		println(html)
	}
}
