require "rubygems"
require "sinatra"
require "sequel"
require "mysql-connector-java-5.1.13-bin.jar"
require "active_support/json"

set :public, File.dirname(__FILE__) + "/jquery-mobile"

DB = Sequel.connect("jdbc:mysql://localhost/information_schema?user=root")

#DBリスト取得
get '/databases' do
	DB["SELECT DISTINCT table_schema FROM tables ORDER BY table_schema"].to_json
end

#指定DBのテーブルリスト取得
get '/tables/:table_schema' do |t|
	sql = "SELECT table_name, table_type, engine, avg_row_length, create_time FROM tables WHERE table_schema=?"
	DB[sql, t].to_json
end

