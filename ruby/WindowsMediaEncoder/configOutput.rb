require 'win32ole'

wmvFile = 'default.wmv'
config = 'config.txt'

if ARGV.length > 0
	wmvFile = ARGV[0]
end

if ARGV.length > 1
	config = ARGV[1]
end

editor = WIN32OLE.new('WMEncEng.WMEncBasicEdit')

editor.MediaFile = File.expand_path(wmvFile)
editor.SaveConfigFile(File.expand_path(config))
