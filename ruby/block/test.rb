[1, 2, 3, 4].each {|i| puts i}

proc = Proc.new {|i| puts i}

puts "-----"

[1, 2, 3, 4].each &proc

puts "-----"

proc2 = lambda {|i| puts i * i}
[1, 2, 3].each &proc2

puts "-----"

def test(num)
	if block_given?
		yield num * 2
		yield num * 4
	end
end

def test2(num, &proc)
	if block_given?
		yield num * 2
		proc.call(num * 2)
		proc.call(num * 4)
	end
end


test(5) do |num|
	puts num
end

puts test(5) #nil

puts "-----"

test2(5) do |num|
	puts num
end

