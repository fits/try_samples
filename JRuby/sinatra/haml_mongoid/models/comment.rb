
class Comment
	include Mongoid::Document

	field :content
	field :created_date, :type => Date

	has_one_related :user

	embedded_in :book, :inverse_of => :comments

end
