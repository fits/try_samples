require "stringio"
require "csv"

export = StringIO.new

CSV::Writer.generate(export) do |csv|
	csv << ["test", "日本語,\"テスト"]
	csv << ["a", "b", "123"]
end

export.rewind

puts export.read
