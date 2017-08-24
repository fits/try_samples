defmodule AppSample.Server do
  use GenServer

  def start_link(num) do
    GenServer.start_link(__MODULE__, num, name: __MODULE__)
  end

  def next do
    GenServer.call(__MODULE__, :next)
  end

  def current do
    GenServer.call(__MODULE__, :current)
  end

  def up(num) do
    GenServer.cast(__MODULE__, {:up, num})
  end


  def handle_call(:next, pid, state) do
    IO.puts "* next pid=#{inspect pid}, state=#{state}"
    { :reply, state + 1, state + 1 }
  end

  def handle_call(:current, _pid, state) do
    { :reply, state, state }
  end

  def handle_cast({:up, num}, state) do
    IO.puts "* :up num=#{num}, state=#{state}"
    { :noreply, state + num }
  end
end
