
p "本".index("{")
p "本" =~ /\{/
p "本" =~ /\{/s

p "田" =~ /c/
p "田" =~ /c/s

puts "--------"

require 'jcode'
$KCODE = 's'

p "本".index("{")
p "本" =~ /\{/

