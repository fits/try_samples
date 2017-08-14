
n = 5

worker = Task.async(fn -> n * n end)

Task.await(worker) |> IO.puts
