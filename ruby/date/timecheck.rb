
def printTime(time)

	print "time : #{time.to_s}\n"

end

class TimeWrapper

	def initialize(year, mon = 1, day = 1, hour = 0, min = 0, sec = 0)
		@time = Time.mktime(year, mon, day, hour, min, sec)
	end

	attr_reader :time

	def add_hours(hours)

		self.add_minutes(hours * 60)

	end

	def add_minutes(minutes)

		@time = @time + (minutes * 60)

	end

end


tm = Time.mktime(2005, 10, 15, 9, 0)

printTime(tm)

tm = tm + 2 * 60 * 60

printTime(tm)


dw = TimeWrapper.new(2005, 10, 15)

dw.add_hours(20)
dw.add_minutes(40)

printTime(dw.time)

dw2 = TimeWrapper.new(2005)

printTime(dw2.time)

