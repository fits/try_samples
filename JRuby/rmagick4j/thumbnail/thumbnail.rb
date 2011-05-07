require 'rubygems'
require 'RMagick'

img = Magick::ImageList.new ARGV[0]

img.resize(64, 64).write(ARGV[1])


