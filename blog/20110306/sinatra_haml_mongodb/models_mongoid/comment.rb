
class Comment
	include Mongoid::Document

	field :content
	field :created_date, :type => Date

	embedded_in :book, :inverse_of => :comments

	belongs_to_related :user
end
