
puts "start"

callcc do |c|
	puts "callcc start"

	c.call

	#以下は実行されない（c.call 時に callcc の終わりにジャンプするため）
	puts "callcc end"

end

puts "end"



puts "start2"

cont = nil

callcc do |c|
	cont = c
	puts "callcc start2"
end

puts "end2"

#以下のコードで callcc が終わった時点に戻るので end2 が無限に出力される
#ただし、JRuby では LocalJumpError が発生する
cont.call
