
def closure(num)
	Proc.new {|i| puts i * num}
end

proc = closure(5)

proc.call(100)

