
print_line = &(String.strip(&1)) |> IO.puts

System.argv() |> List.first |> File.stream! |> Enum.each print_line
