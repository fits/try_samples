require "rexml/document"

doc = REXML::Document.new File.new(ARGV[0])

values = doc.get_elements("//jpfr-t-cte:OperatingIncome[@contextRef='CurrentYearConsolidatedDuration']")

values.each do |n|
	puts n.text
end
