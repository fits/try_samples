require "thread"
require "uri"
require "net/http"

if (ARGV.length() < 1)
	puts "ruby #{__FILE__} <dir>"
	exit
end

poolSize = 5

dir = ARGV[0]

q = Queue.new
$stdin.readlines.each {|l| q.push(l.chomp)}

threads = []
poolSize.times do
	threads << Thread.start(q) do |tq|
		while not q.empty?
			u = q.pop(true)

			begin
				url = URI.parse(u)
				fileName = File.basename(url.path)
				filePath = File.join(dir, fileName)

				res = Net::HTTP.get_response(url)
				open(filePath, 'wb') {|f| f.puts res.body}

				puts "completed: #{u}"
			rescue => e
				puts "failed: #{u}, #{e}"
			end
		end
	end
end

threads.each {|t| t.join}

