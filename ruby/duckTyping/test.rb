
class Item

	def initialize(point)
		@point = point
	end

	def calculate
		@point
	end
end

class PointData

	def calculate
		1000
	end

end

class ListItem < Item

	def initialize(point)
		super(point)
		@items = []
	end

	def addItem(item)
		@items.push item
	end

	def calculate
		result = @point

		@items.each {|it|
			result += it.calculate
		}
		result
	end
end

its = ListItem.new(100)
its.addItem(Item.new(10))
its.addItem(Item.new(5))

its2 = ListItem.new(1)
its.addItem(its2)

its2.addItem(PointData.new)

puts its.calculate
