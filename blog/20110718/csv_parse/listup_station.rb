
prefMap = Hash[IO.readlines("m_pref.csv").drop(1).map {|l| l.chop.split(',')}]

list = IO.readlines("m_station.csv").drop(1).map {|l|
	l.chop.split(',')
}.group_by {|s|
	[s[9], prefMap[s[10]], s[5]]
}.sort {|a, b|
	b[1].length <=> a[1].length
}.take 10

list.each do |s|
	puts "#{s[0][0]}‰w (#{s[0][1]}) : #{s[1].length}"
end
