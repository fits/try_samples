@Grab('org.apache.tika:tika-parsers:1.16')
@Grab('org.slf4j:slf4j-nop:1.7.25')
@GrabExclude('com.google.guava#guava;17.0')
@GrabExclude('org.codehaus.plexus#plexus-utils;1.5.6')
import org.apache.tika.parser.html.HtmlParser
import org.apache.tika.sax.BodyContentHandler
import org.apache.tika.metadata.Metadata

def handler = new BodyContentHandler()
def meta = new Metadata()

def parser = new HtmlParser()

parser.parse(System.in, handler, meta)

println handler.toString()
