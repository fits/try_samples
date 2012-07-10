@Grab('com.madgag:markdownj-core:0.4.1')
import com.petebevin.markdown.*

if (args.length < 2) {
	println "groovy tohtml.groovy <markdown file> <output file>"
	System.exit(0)
}

def p = new MarkdownProcessor()
def enc = 'UTF-8'

new File(args[1]).setText(p.markdown(new File(args[0]).getText(enc)), enc)

