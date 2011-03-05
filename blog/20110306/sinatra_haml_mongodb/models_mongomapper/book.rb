
class Book
	include MongoMapper::Document

	# デフォルトで _id は ObjectId 型になるので String を指定
	key :_id, String
	key :title, String
	key :isbn, String

	many :comments
end
