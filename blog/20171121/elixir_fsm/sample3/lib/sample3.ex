defmodule Sample3 do
  def main(_args) do
    {:ok, pid} = GenStateMachine.start_link(StateTimeoutStateMachine, nil)

    GenStateMachine.cast(pid, :on)
    GenStateMachine.cast(pid, :off)

    GenStateMachine.cast(pid, :off)

    GenStateMachine.cast(pid, :on)

    :timer.sleep(2500)

    GenStateMachine.cast(pid, :on)

    :timer.sleep(1500)

    GenStateMachine.cast(pid, :invalid_message)

    :timer.sleep(1500)

    GenStateMachine.cast(pid, :invalid_message)

    :timer.sleep(2500)

    GenStateMachine.stop(pid)
  end
end
