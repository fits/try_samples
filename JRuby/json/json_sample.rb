#coding:utf-8

require 'json/pure'

data = {
	:name => "test1テスト",
	:point => 10
}

doc = JSON.generate data

puts doc

obj = JSON.parse doc

p obj
