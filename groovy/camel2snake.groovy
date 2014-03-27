
def toSnake = {
	it.replaceAll(/([A-Z]+)/, /_$1/).replaceAll(/^_/, '')
}

def toUpper = { it.toUpperCase() }

def toSnakeUpper = toSnake >> toUpper

println toSnakeUpper(args[0])
