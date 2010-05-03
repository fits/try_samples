#指定文字の文字コードを出力する

if ARGV.length() < 1
	puts ">ruby #{__FILE__} [character]"
	exit
end

print '0x'
ARGV[0].each_byte do |b|
	print sprintf("%x", b)
end

