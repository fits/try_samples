
require 'rubygems'
require 'rparsec'

include RParsec::Parsers

eol = string "\r\n"
quotedChar = not_char('"') | string('""')
quotedCell = char('"') >> quotedChar.many.fragment << char('"')
cell = quotedCell | regexp(/[^,\r\n]*/).fragment
line = cell.separated(char ',')
csvFile = (line << eol).many

cs = $stdin.readlines.join
res = csvFile.parse cs

p res
puts res
