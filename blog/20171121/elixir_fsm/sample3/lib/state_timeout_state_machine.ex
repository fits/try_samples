defmodule StateTimeoutStateMachine do
  use GenStateMachine

  def init(_args) do
    {:ok, :idle, 0}
  end

  def handle_event(:cast, :on, :idle, data) do
    IO.puts "*** :on, idle -> active"

    actions = [{:state_timeout, 2000, :off}]
    {:next_state, :active, data + 1, actions}
  end

  def handle_event(:cast, :off, :active, data) do
    IO.puts "*** :off, active -> idle"
    {:next_state, :idle, data}
  end

  def handle_event(:state_timeout, :off, :active, data) do
    IO.puts "*** :state_timeout, active -> idle"
    {:next_state, :idle, data}
  end

  def handle_event(event_type, event_content, state, data) do
    IO.puts "*** Unhandled: type=#{event_type}, content=#{event_content}, state=#{state}, data=#{data}"
    {:keep_state, data}
    # {:keep_state_and_data, []}
  end
end
