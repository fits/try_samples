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
class SendMail < ActionMailer::Base
	def send_mail(from, to, subject)
		body = $stdin.readlines.join

		mail(:from => from, :to => to, :subject => subject, :body => body, :enable_starttls_auto => false)
	end
end

SendMail.send_mail(ARGV[1], ARGV[2], ARGV[3]).deliver

