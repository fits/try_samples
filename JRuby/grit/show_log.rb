
require 'rubygems'
require 'grit'
require 'iconv'

include Grit

repo = Repo.new("E:\\test_develop")

repo.commits.each do |cm|
	puts "------ id : #{cm.id} ------\n"
	puts Iconv.conv('Shift_JIS', 'UTF-8', cm.message)
end
