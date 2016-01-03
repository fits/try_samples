
plus = fn a, b -> a + b end
plus2 = &(&1 + &2)

plus.(5, 8) |> IO.puts
plus2.(6, 10) |> IO.puts


defmodule Sample do
	def plus(a, b) do
		a + b
	end
end

plus3 = &Sample.plus/2

Sample.plus(7, 11) |> IO.puts
plus3.(20, 1) |> IO.puts
