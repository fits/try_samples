
class User
	include MongoMapper::Document

	# デフォルトで _id は ObjectId 型になるので String を指定
	key :_id, String
	key :name
end
