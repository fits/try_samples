require 'kramdown'

Encoding.default_external = 'utf-8'

puts Kramdown::Document.new($stdin.read).to_html
