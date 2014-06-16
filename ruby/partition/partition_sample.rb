# coding: utf-8

list = ['a', '1', 'b', '2', 'c', '3']

plist1 = list.partition do |n|
	['a', 'c'].include?(n)
end

p plist1

func = lambda { |n| ['a', 'c'].include?(n) }

# lambda を partition の引数へ指定するには & を付ける必要あり（Proc も同様）
plist2 = list.partition(&func)

p plist2
