# encoding: utf-8
require "logstash/outputs/base"
require "logstash/namespace"

class LogStash::Outputs::Sample < LogStash::Outputs::Base
	config_name "sample"

	public

	def register
		puts "*** register"
	end

	def multi_receive(events)
		puts "*** multi_receive"
		puts events
	end

end