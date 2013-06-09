# -*- encoding: utf-8 -*-
require 'rubygems'
require 'sequel'
require 'sequel/adapters/jdbc'
require 'sequel/adapters/jdbc/oracle'
require_relative 'lib/ojdbc14-10.2.0.3.0.jar'

class Sequel::JDBC::Oracle::Dataset
	# メソッドをオーバーライド
	def convert_type_oracle_timestamp(v)
		db.to_application_timestamp(v.to_jdbc.to_string)
		# 以下でも可
		# db.to_application_timestamp(v.timestamp_value.to_string)
	end
end

DB = Sequel.connect('jdbc:oracle:thin:user1/pass1@localhost:1521/XE')

order = DB[:sample_order]

ds = order.where { |o| o.value > 200 }

puts ds.sql

ds.all.each do |r|
	p r
end
