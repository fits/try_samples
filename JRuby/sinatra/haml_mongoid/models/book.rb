
class Book
	include Mongoid::Document

	field :title
	field :isbn

	embeds_many :comments

end
