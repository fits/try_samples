require "win32ole"

encoder = WIN32OLE.new("WMEncEng.WMEncoder")

profile = nil

encoder.ProfileCollection.each {|pf|
	if pf.Name.include?('Video 8 for Local Area Network')
		profile = pf
	end
}

encoder.File.LocalFileName = File.expand_path('default_new.wmv')

cols = encoder.SourceGroupCollection

g1 = cols.Add('SG_1')
g1.Profile = profile
g1.AutoSetFileSource(File.expand_path('default.wmv'))

g2 = cols.Add('SG_2')
g2.Profile = profile
g2.AutoSetFileSource(File.expand_path('default.wmv'))
g2.SetAutoRollover(-1, g1.Name)

cols.Active = g2

encoder.PrepareToEncode(true)
encoder.Start

while true

	if encoder.RunState == 5
		break
	end

	puts "ïœä∑èàóùíÜÅEÅEÅE(#{encoder.RunState})\n"
	sleep(5)
end
