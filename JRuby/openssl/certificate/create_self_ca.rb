require 'webrick/ssl'

cn = [ [ "CN", WEBrick::Utils::getservername ] ]
comment = "Test"

cert, rsa = WEBrick::Utils::create_self_signed_cert(1024, cn, comment)

open('test.cer', 'wb') {|f| f.puts cert.to_s}
open('test.key', 'wb') {|f| f.puts rsa.to_s}
