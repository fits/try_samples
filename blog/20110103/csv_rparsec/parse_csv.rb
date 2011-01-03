
require 'rubygems'
require 'rparsec'

include RParsec::Parsers

eol = string "\r\n"
quotedChar = not_char('"') | string('""') >> value('"'[0])
quotedCell = char('"') >> quotedChar.many.bind {|s| value(s.pack("c*"))} << char('"')
cell = quotedCell | regexp(/[^,\r\n]*/)
line = cell.separated(char ',')
csvFile = (line << eol).many

cs = $stdin.readlines.join
res = csvFile.parse cs

p res
puts res
