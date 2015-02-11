@Grab('org.jparsec:jparsec:2.2.1')
import org.codehaus.jparsec.Scanners

def eol = Scanners.isChar('\r' as char).next(Scanners.isChar('\n' as char)).or(Scanners.isChar('\n' as char)).or(Scanners.isChar('\r' as char))

def cell = Scanners.IDENTIFIER.or(Scanners.DECIMAL).sepBy(Scanners.isChar(',' as char))

def csv = cell.endBy(eol)

println csv.parse(System.in.text)
