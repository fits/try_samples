defmodule Sample do
  @moduledoc """
  Documentation for Sample.
  """

  def hello do
    :world
  end

  def main(argv) do
    IO.puts Sample.hello
  end
end
