require "rubygems"
require "action_mailer"

if ARGV.length() != 4
	puts "args: <SMTP Server> <From> <To> <Subject>"
	exit
end

ActionMailer::Base.smtp_settings = {
	:address => ARGV[0],
	:enable_starttls_auto => false
}

subject = ARGV[3].encode("UTF-8", "Shift_JIS")
body = $stdin.readlines.join.encode("UTF-8", "Shift_JIS")

ActionMailer::Base.mail(:from => ARGV[1], :to => ARGV[2], :subject => subject, :body => body).deliver

