@Grab('net.sourceforge.pmd:pmd-java:5.5.3')
import net.sourceforge.pmd.PMD

def params = [
	'-d',
	args[0],
	'-f',
	'text',
	'-R',
	'java-codesize'
] as String[]

PMD.main(params)
