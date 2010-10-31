
#文字コードの変換処理
# >jruby --1.9 encode.rb < ファイル名
puts $stdin.readlines.join.encode("UTF-8", "Shift_JIS")

