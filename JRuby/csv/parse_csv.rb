require "csv"

CSV.open(ARGV[0], 'r') do |r|
	puts "#{r[1]} : #{r[2]}"
end
