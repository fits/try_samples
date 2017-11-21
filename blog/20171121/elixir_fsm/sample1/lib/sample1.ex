defmodule Sample1 do
  def main(_args) do
    {:ok, pid} = GenStateMachine.start_link(SampleStateMachine, nil)

    GenStateMachine.cast(pid, :on)
    GenStateMachine.cast(pid, :off)

    GenStateMachine.cast(pid, :off)

    GenStateMachine.stop(pid)
  end
end
