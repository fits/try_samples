
require 'rubygems'
require 'rparsec'

include RParsec::Parsers

eol = string "\r\n"
cell =  not_string("\r\n").many.fragment
#line = cell.separated(char ",")
csvFile = (cell << eol).many

cs = $stdin.readlines.join
res = csvFile.parse cs

p cs
p res
