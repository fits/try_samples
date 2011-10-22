
f = Fiber.new do
	10.times do |t|
		puts "times loop"
		Fiber.yield t
	end
end

puts f.resume
puts "-----"
puts f.resume


