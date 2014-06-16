
def split_each_byte(fileName, size)
	Enumerator.new do |y|
		File.open(fileName, 'rb') do |file|
			while (buf = file.read(size)) do
				y << buf
			end
		end
	end
end

def write_file(fileName, list)
	File.open(fileName, 'wb') do |file|
		list.each do |buf|
			file.write buf
		end
	end
end

list = split_each_byte(ARGV[0], 10).partition { |buf| buf[4, 1] == '0' }

write_file 'r1.dat', list.first()
write_file 'r2.dat', list[list.count() - 1]
