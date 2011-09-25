require "thread"
require "uri"
require "net/http"

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
				filePath = File.join(dir, File.basename(url.path))

				res = Net::HTTP.get_response(url)
				open(filePath, 'wb') {|f| f.puts res.body}

				puts "downloaded: #{url} => #{filePath}"
			rescue => e
				puts "failed: #{url}, #{e}"
			end
		end
	end
end

threads.each {|t| t.join}
