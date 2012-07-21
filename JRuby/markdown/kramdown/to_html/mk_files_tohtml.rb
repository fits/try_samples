
require 'kramdown'

Dir.glob("#{ARGV[0]}/**/*.markdown") {|file|
    File.open(file) {|f|
        body = Kramdown::Document.new(f.read()).to_html

        html = <<-"EOS"
<!DOCTYPE html>
<html>
<body>
  #{body}
</body>
</html>
EOS

        destFile = "#{File.dirname(file)}/#{File.basename(file, '.*')}.html"

        File.open(destFile, 'w') {|df|
            df.write(html)
        }

        puts "create #{destFile}"
    }
}
