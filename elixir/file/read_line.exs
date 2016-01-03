
System.argv() |> List.first |> File.stream! |> Enum.map(&String.strip/1) |> Enum.each(&IO.puts/1)
