#指定文字の文字コードを出力する

require 'iconv'

if ARGV.length() < 1
	puts ">ruby #{__FILE__} [character]"
	exit
end

msg = Iconv.conv("UTF-8", "Shift_JIS", ARGV[0])

p msg

print '0x'
msg.each_byte do |b|
	print sprintf("%X", b)
end

