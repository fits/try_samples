defmodule Sample do
  use GenServer

  def handle_call(:next, pid, state) do
    IO.puts "* next pid=#{inspect pid}, state=#{state}"
    { :reply, state + 1, state + 1 }
  end

  def handle_call(:current, pid, state) do
    { :reply, state, state }
  end

  def handle_cast({:up, num}, state) do
    IO.puts "* :up num=#{num}, state=#{state}"
    { :noreply, state + num }
  end
end
