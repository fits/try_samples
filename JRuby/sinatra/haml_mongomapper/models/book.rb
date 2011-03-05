
class Book
	include MongoMapper::Document

	key :title, String
	key :isbn, String

	many :comments

end
