#coding:UTF-8
require "rubygems"
require "action_mailer"

ActionMailer::Base.smtp_settings = {
	#SMTPサーバー設定
	:address => ARGV[0],
	:enable_starttls_auto => false
}

subject = ARGV[3].encode("UTF-8", "Shift_JIS")
#標準入力を文字列化
body = $stdin.readlines.join.encode("UTF-8", "Shift_JIS")

ActionMailer::Base.mail(:from => ARGV[1], :to => ARGV[2], :subject => subject, :body => body).deliver

