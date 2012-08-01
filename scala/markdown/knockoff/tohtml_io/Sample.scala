package fits.sample

import scalax.io.JavaConverters._
import com.tristanhunt.knockoff.DefaultDiscounter._

object Sample extends App {

	val mkStr = System.in.asUnmanagedInput.slurpString

	val html = toXHTML(knockoff(mkStr)).mkString

	System.out.asUnmanagedOutput.write(html)
}
