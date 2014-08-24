
import java.text.SimpleDateFormat
import java.time.{ LocalDateTime, OffsetDateTime, ZonedDateTime, ZoneOffset }
import java.util.Date

val funcList = List(
	(s: String) => Date.from(LocalDateTime.parse(s).toInstant(ZoneOffset.UTC)),
	(s: String) => Date.from(OffsetDateTime.parse(s).toInstant()),
	(s: String) => Date.from(ZonedDateTime.parse(s).toInstant()),
	(s: String) => s match {
		case "now" => new Date()
	}
)

val eitherK = (func: String => Date) => (s: String) => try {
	func(s) match {
		case d: Date => Right(d)
		case _ => Left(s)
	}
} catch {
	case e: Exception => Left(s)
}

val dstr: Either[String, Date] = Left(args(0))

val res = funcList.foldLeft(dstr) { (acc, func) =>
	acc.left.flatMap( eitherK(func) )
}

println(res)
