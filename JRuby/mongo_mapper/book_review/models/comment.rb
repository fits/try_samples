
class Comment
	include MongoMapper::EmbeddedDocument

	key :content, String
	key :created_date, Date

	belongs_to :user
end
