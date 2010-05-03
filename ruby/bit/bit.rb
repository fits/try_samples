
def printList(list)

	for i in list
		print "#{i}"
	end

	print "\n"
end

def bitDivide(list, num)

	if num < 2
		list.push(num)
	else
		temp = num / 2

		list.push(num - (temp * 2))

		bitDivide(list, temp)
	end
end

list = Array.new

bitDivide(list, 5)

printList(list)

list.clear

bitDivide(list, 24)

printList(list)
