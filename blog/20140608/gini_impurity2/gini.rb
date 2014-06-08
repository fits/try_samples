#coding:utf-8

# (a) 1 - (AA + BB + CC)
def giniA(xs)
	1 - xs.group_by {|x| x }.inject(0) {|a, (k, v)| a + (v.size.to_f / xs.size) ** 2 }
end

# (b) AB × 2 + AC × 2 +  BC × 2
def giniB(xs)
	xs.group_by {|x| x }.map {|k, v| [k, v.size.to_f / xs.size] }.combination(2).inject(0) {|a, t| a + t.first.last * t.last.last * 2}
end

list = ["A", "B", "B", "C", "B", "A"]

puts giniA(list)
puts giniB(list)
