package sample

import scalaz._
import Scalaz._
import scalaz.effect._
import Effect._

import java.io.{BufferedReader, FileReader}

object FileRead extends App {

	val r = for {
		file <- IO { new BufferedReader(new FileReader(args(0))) }
		x <- IO { file.readLine() }
		_ <- IO { file.readLine() }
		y <- IO { file.readLine() }
		_ <- closeableResource.close(file)

	} yield (x, y)

	r.unsafePerformIO |> println
}