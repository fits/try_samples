require 'java'

module Jude
	include_package "com.change_vision.jude.api.inf.project"
	include_package "com.change_vision.jude.api.inf.model"
end

pro = Jude::ProjectAccessorFactory.getProjectAccessor

pro.open "test.jude"

model = pro.getProject

puts "--- Diagrams ---"
model.getDiagrams.each do |dg|
	puts dg.getName
end

puts "--- UseCase ---"
model.getOwnedElements.each do |oe|
	puts oe.name if oe.java_kind_of? Jude::IUseCase
end

pro.close

