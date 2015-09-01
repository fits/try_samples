
defmodule Sample do
	def sample(0) do
		IO.puts "zero"
	end

	def sample(1) do
		IO.puts "one"
	end

	def sample(x) do
		IO.puts x
	end
end

Sample.sample 0
Sample.sample 1
Sample.sample 2
Sample.sample "sample"
