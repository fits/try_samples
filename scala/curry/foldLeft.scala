
var res = args.foldLeft(0)((sum, arg) => sum + arg.toInt)
println(res)

println(args.foldLeft("0")((str, arg) => str + ", " + arg))

