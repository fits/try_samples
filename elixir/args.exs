
IO.puts System.argv()

IO.puts '---'

System.argv() |> Enum.each(&(IO.puts &1))

IO.puts '---'

System.argv() |> Enum.each(fn x -> IO.puts x end)
