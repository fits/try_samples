require "rubygems"
require "active_record"

ActiveRecord::Base.establish_connection(
	:adapter => "mysql", :host => "localhost", 
	:user => "root", :password => "", :database => "test"
)

#実行時のエラー回避用
ActiveRecord::Base.logger = Logger.new(STDOUT)

class Test < ActiveRecord::Base
	#テーブル名
	set_table_name :test1
end

t = Test.create([
	{:id => "a1", :name => "testdata"}
])

t2 = Test.new(:id => "b2", :name => "testb")
t2.save
