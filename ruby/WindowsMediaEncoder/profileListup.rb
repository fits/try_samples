require "win32ole"

encoder = WIN32OLE.new("WMEncEng.WMEncoder")

encoder.ProfileCollection.each {|pf|
	puts "#{pf.Name}\n"
}

