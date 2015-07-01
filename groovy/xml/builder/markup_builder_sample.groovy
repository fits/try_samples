import groovy.xml.MarkupBuilder

def key = args[0]
def worksheetId = args[1]
def row = args[2] as int
def col = args[3] as int
def value = args[4]

def sw = new StringWriter()

def xml = new MarkupBuilder(sw).entry(
	xmlns: 'http://www.w3.org/2005/Atom',
	'xmlns:gs': 'http://schemas.google.com/spreadsheets/2006'
) {
	id("https://spreadsheets.google.com/feeds/cells/${key}/${worksheetId}/private/full/R${row}C${col}")

	link (rel: 'edit', type: 'application/atom+xml', href: "https://spreadsheets.google.com/feeds/cells/${key}/${worksheetId}/private/full/R${row}C${col}")

	'gs:cell' (row: row, col: col, inputValue: value)
}

println sw.toString()
