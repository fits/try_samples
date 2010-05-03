
classList = ObjectSpace.each_object do |o|
	p o if o.is_a? Class
end
