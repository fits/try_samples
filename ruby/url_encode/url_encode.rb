require 'uri'
require 'cgi'

str = ';/?:@=&% $-_.+!*\'"(),{}|\\^~[]'

# ;/?:@=&%25%20$-_.+!*'%22(),%7B%7D%7C%5C%5E~[]
puts URI.escape(str)
puts URI.encode(str)

# %3B%2F%3F%3A%40%3D%26%25+%24-_.%2B%21%2A%27%22%28%29%2C%7B%7D%7C%5C%5E%7E%5B%5D
puts CGI.escape(str)
