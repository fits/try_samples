
require 'rubygems'
require 'rparsec'

include RParsec::Parsers

eol = string "\r\n"
cell =  regexp(/[^,\r\n]*/).fragment
line = cell.separated(string ",")
csvFile = (line << eol).many

cs = $stdin.readlines.join
res = csvFile.parse cs

p res
