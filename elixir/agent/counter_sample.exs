defmodule Counter do
	@name __MODULE__

	def start_link do
		Agent.start_link(fn -> %{} end, name: @name)
	end

	def count_up(key, value \\ 1) do
		Agent.update(@name, fn map -> Map.update(map, key, value, &(&1 + value)) end)
	end

	def current_count(key) do
		Agent.get(@name, fn map -> map[key] end)
	end

	def keys do
		Agent.get(@name, fn map -> Map.keys(map) end)
	end

end

Counter.start_link

Counter.count_up "a1"

Counter.current_count("a1") |> IO.puts

Counter.count_up "a1"

Counter.current_count("a1") |> IO.puts

Counter.count_up "a1", 5

Counter.current_count("a1") |> IO.puts

Counter.count_up "b2", 3

Counter.current_count("b2") |> IO.puts

Counter.keys |> Enum.each(&IO.puts/1)
