
n = 10

1..n |> Enum.each(&IO.puts/1)

IO.puts "-----"

1..n |> Enum.map(&(&1 * &1)) |> Enum.sum |> IO.puts

IO.puts "-----"

1..n |> Enum.map(&(&1 * &1)) |> Enum.reduce(&(&1 + &2)) |> IO.puts
