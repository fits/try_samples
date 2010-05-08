require "rexml/document"
require "uconv"

file = File.new("test_s.xml")
doc = REXML::Document.new file

doc.get_elements("QUESTION/ANSWER").each { |element|

	element.children.each { |el|

		if el.node_type == :element then
			puts "#{el.name}, #{el.text}"
#			puts "#{el.name}, #{Uconv.u8tosjis(el.text)}"
		end

	}
}
