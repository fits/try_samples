require "csv"

CSV.open(ARGV[0], 'r', "\t") do |r|
	puts "#{r[1]} : #{r[2]}"
end
