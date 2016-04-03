
class A
	def self.m1
		'A-m1'
	end

	def m2
		'A.m2'
	end
end

module B
	def m2
		"B.m2 - #{super}"
	end
end

module C
	def m1
		"C.m1 $ #{super}"
	end
end

A.prepend B

#class A
#	prepend B
#end

A.singleton_class.prepend C

#class << A
#	prepend C
#end

puts A.new.m2

puts A.m1
