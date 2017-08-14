
n = 10

1..n 
	|> Stream.map(fn x -> Task.async(fn -> x * x end) end)
	|> Stream.map(&Task.await/1)
	|> Enum.sum
	|> IO.puts
