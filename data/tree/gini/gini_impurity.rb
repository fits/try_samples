
def gini1(xs)
	1 - xs.group_by {|x| x}.inject(0) {|a, (k, v)| a + (v.size.to_f / xs.size) ** 2 }
end

def gini2(xs)
	xs.group_by {|x| x}.map {|k, v| [k, v.size.to_f / xs.size]}.combination(2).inject(0) {|a, t| a + t.first.last * t.last.last * 2}

end

list = ["A", "B", "B", "C", "B", "A"]

puts gini1(list)
puts gini2(list)
