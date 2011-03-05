
class User
	include MongoMapper::Document

	key :name, String

	#has_many :comments

end
