
class Comment
	include MongoMapper::EmbeddedDocument

	# デフォルトで _id は ObjectId 型になるので String を指定
	key :_id, String
	key :content
	key :created_date, Date

	belongs_to :user
end
