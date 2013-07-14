@Grab('info.cukes:cucumber-groovy:1.1.3')
import cucumber.api.cli.Main

if (args.length < 3) {
	println 'groovy -c UTF-8 run_cucumber.groovy --glue <scripts dir> <features dir>'
	return
}

def loader = Thread.currentThread().getContextClassLoader()

Main.run(args, loader)
