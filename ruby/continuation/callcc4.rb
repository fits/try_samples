
puts "start"

res = callcc do |c|
	puts "before c"

	i = c.call(c.call(1))
	puts "i = #{i}"
	i
end + 1

puts "after : #{res}"

