
class A
	def self.m1
		'A-m1'
	end

	def m2
		'A.m2'
	end
end

class B < A
	def self.m1
		"B-#{super}"
	end

	def m2
		"B.#{super}"
	end

end

puts A.m1
puts A.new.m2

puts '-----'

puts B.m1
puts B.new.m2

bm = B.new.method(:m2)

puts '-----'

module C
	def self.included(base)
		base.extend StaticMethods
	end

	module StaticMethods
		def m1
			" =C= #{super}"
		end
	end

	def m2
		" _C_ #{super}"
	end

	def m3
		'C.m3'
	end
end

class B
	include C
end

puts B.m1
puts B.new.m2
puts B.new.m3
puts bm.call
