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

actors = []
usecases = {}

pro.project.ownedElements.each do |oe|
	actors.push(oe) if oe.stereotypes.include?("actor")
	usecases.store(oe.name, oe) if oe.java_kind_of? Jude::IUseCase
end

actors.each do |actor|
	puts "actor : #{actor.name}"

	actor.attributes.each do |ass|

		if ass.association
			uc = usecases[ass.typeExpression]
			puts "usecase : #{uc.name}"
		end
	end

end

pro.close

