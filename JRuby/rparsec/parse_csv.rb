
require 'rubygems'
require 'rparsec'

include RParsec::Parsers

eol = string "\r\n"
#packで文字列化できるように、" の文字コードを返すようにしている（value の箇所）
quotedChar = not_char('"') | string('""') >> value('"'[0])
#manyで文字コードの配列が結果的に返るため、packを使って文字列化
quotedCell = char('"') >> quotedChar.many.bind {|s| value(s.pack("c*"))} << char('"')
cell = quotedCell | regexp(/[^,\r\n]*/)
line = cell.separated(char ',')
csvFile = (line << eol).many
#一応、以下でも可（ただし、結果が少し変わる）
#csvFile = line.delimited(eol)

cs = $stdin.readlines.join
res = csvFile.parse cs

p res
puts res
