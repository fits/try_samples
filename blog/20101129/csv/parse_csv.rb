require "csv"

CSV.foreach(ARGV[0]) do |r|
	puts "#{r[0]} : #{r[2]}"
end
