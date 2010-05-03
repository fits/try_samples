
def loop(time)
	for i in 1..10
		puts "#{i}"
		
		if i == time
			callcc do |c|
				return c
			end
		end
	end
end


puts "before loop"
cont = loop 5
puts "after loop"

if cont.respond_to? "call"
	puts "call"
	cont.call
end
