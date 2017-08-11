
defmodule Sample do
	def print(msg) do
		IO.puts msg
	end
end

Sample.print "sample1"

pid = spawn(Sample, :print, ["sample2"])

IO.puts(inspect pid)
