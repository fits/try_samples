@Grab('com.madgag:markdownj-core:0.4.1')
import com.petebevin.markdown.*

def p = new MarkdownProcessor()

println p.markdown('#test')

