defmodule TimeoutStateMachine do
  use GenStateMachine

  def init(_args) do
    {:ok, :idle, 0}
  end

  def handle_event(:cast, :on, :idle, data) do
    IO.puts "*** :on, idle -> active"
    {:next_state, :active, data + 1, 2000}
  end

  def handle_event(:cast, :off, :active, data) do
    IO.puts "*** :off, active -> idle"
    {:next_state, :idle, data}
  end

  def handle_event(:timeout, event_content, :active, data) do
    IO.puts "*** :timeout content=#{event_content}, active -> idle"
    {:next_state, :idle, data}
  end

  def handle_event(event_type, event_content, state, data) do
    IO.puts "*** Unhandled: type=#{event_type}, content=#{event_content}, state=#{state}, data=#{data}"
    {:keep_state, data}
  end
end
