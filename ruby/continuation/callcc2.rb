
cont = nil

i = callcc do |c|
	cont = c
	0
end

puts i

cont.call(i + 1) if i < 10
