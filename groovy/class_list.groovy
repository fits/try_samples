import javax.tools.*

if (args.length < 1) {
	println "<package name>"
	return
}

def compiler = ToolProvider.systemJavaCompiler
def fm = compiler.getStandardFileManager(null, null, null)

def locations = [StandardLocation.PLATFORM_CLASS_PATH, StandardLocation.CLASS_PATH]

locations.each { location ->
	fm.list(location, args[0], [JavaFileObject.Kind.CLASS] as Set, true).each {
		println it.name
		println it.toUri()
		println '----------'
	}
}
