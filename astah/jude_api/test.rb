require 'java'

if ARGV.empty?
	puts ">jruby #{__FILE__} [jude file]"
	exit
end

module Jude
    include_package "com.change_vision.jude.api.inf.project"
    include_package "com.change_vision.jude.api.inf.model"
end

pro = Jude::ProjectAccessorFactory.projectAccessor

pro.open ARGV[0]

pro.project.ownedElements.each do |oe|
    puts "actor : #{oe.name}" if oe.stereotypes.include?("actor")
    puts "usecase : #{oe.name}" if oe.java_kind_of? Jude::IUseCase
end

pro.close
