defmodule AppSample.Application do
  @moduledoc false

  use Application

  def start(_type, _args) do
    children = [
      {AppSample.Server, 0}
    ]

    opts = [strategy: :one_for_one, name: AppSample.Supervisor]
    Supervisor.start_link(children, opts)
  end
end
