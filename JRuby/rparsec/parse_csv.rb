
require 'rubygems'
require 'rparsec'

include RParsec::Parsers

eol = string "\r\n"
quotedChar = not_char('"').many.fragment
quotedCell = char('"') >> quotedChar << char('"')
cell = quotedCell | regexp(/[^,\r\n]*/).fragment
line = cell.separated(char ',')
csvFile = (line << eol).many

cs = $stdin.readlines.join
res = csvFile.parse cs

p res
