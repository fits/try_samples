@Grab('com.madgag:markdownj-core:0.4.1')
import com.petebevin.markdown.*

def mk = new MarkdownProcessor()
def enc = 'UTF-8'

System.out.withWriter enc, {w ->
	w.print mk.markdown(System.in.getText(enc))
}
