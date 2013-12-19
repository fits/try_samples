@Grab("org.jsoup:jsoup:1.7.3")
import org.jsoup.Jsoup

if (args.length < 2) {
	println '<url> <selector>'
	return
}

def doc = Jsoup.connect(args[0]).get()

println doc.select(args[1]).dump()
