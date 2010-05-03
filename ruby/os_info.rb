puts RUBY_PLATFORM

#RUBY_PLATFORM = "mswin32(java)"
#RUBY_PLATFORM.instance_variable_set("mswin32(java)")

#puts RUBY_PLATFORM

if (RUBY_PLATFORM == "java") 
	require "java"
	os = java.lang.System.getProperty("os.name")

	RUBY_PLATFORM = "mswin(java)" if os.downcase.include?("windows")
end

puts RUBY_PLATFORM
