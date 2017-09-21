defmodule StateMachineSample do
  use GenStateMachine

  def init(_args) do
    {:ok, :idle, nil}
  end

  def handle_event(:cast, :on, :idle, data) do
    IO.puts ":on, idle -> active"
    {:next_state, :active, data, 2000}
  end

  def handle_event(:timeout, event_content, :active, data) do
    IO.puts ":timeout, #{event_content}, active -> idle"
    {:next_state, :idle, data}
  end

  def handle_event(:cast, :off, :active, data) do
    IO.puts ":off, active -> idle"
    {:next_state, :idle, data}
  end

  def handle_event(event_type, event_content, state, data) do
    IO.puts "ignore: #{event_type}, #{event_content}, #{state}, #{data}"
    {:keep_state, data}
  end

  def run() do
    {:ok, pid} = GenStateMachine.start_link(StateMachineSample, nil)

    GenStateMachine.cast(pid, :on)
    GenStateMachine.cast(pid, :off)

    GenStateMachine.cast(pid, :off)

    GenStateMachine.cast(pid, :on)
    :timer.sleep(3000)
    GenStateMachine.cast(pid, :off)

    GenStateMachine.stop(pid)
  end
end
