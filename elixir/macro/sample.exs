
defmodule A do
	defmacro macro_sample(expr, block) do
		quote do
			if unquote(expr) do
				unquote(block)
			end
		end
	end
end

defmodule Main do
	require A

	def main() do
		IO.puts "*** call macro"

		A.macro_sample false, IO.puts "no print"
		A.macro_sample true, IO.puts "print test1"
	end
end

Main.main