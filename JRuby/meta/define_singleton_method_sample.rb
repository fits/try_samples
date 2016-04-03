
class A
	# class method
	def self.m1
		'A-m1'
	end

	def m2
		'A.m2'
	end
end

a = A.new

a.define_singleton_method(:m3) { "m3 - #{self.m2}, #{self.class.m1}" }

A.define_singleton_method(:m4) { "m4 - #{m1}" }

puts a.m3

puts A.m4
