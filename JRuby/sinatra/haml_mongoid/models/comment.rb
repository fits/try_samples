
class Comment
	include Mongoid::Document

	field :content
	field :created_date, :type => Date

#	has_one_related で User に belongs_to_related を使うと
#   エラーが発生するため、とりあえず field で代用
#
#	has_one_related :user
	field :user, :type => User

	embedded_in :book, :inverse_of => :comments

end
