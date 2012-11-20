
module Sample
	refine String do
		def sample()
			self + "!"
		end
	end
end

class SampleTest
	using Sample

	def echo(msg)
		puts msg.sample
	end
end 

# test!
SampleTest.new.echo("test")
