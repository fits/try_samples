@Grab("org.jsoup:jsoup:1.7.3")
import org.jsoup.Jsoup

if (args.length < 2) {
	println '<html file> <selector>'
	return
}

def doc = Jsoup.parse(new File(args[0]).newInputStream(), null, '')

println doc.select(args[1]).dump()
