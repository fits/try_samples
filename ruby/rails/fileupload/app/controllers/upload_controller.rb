class UploadController < ApplicationController

	def regist()

		dir = 'work_data'
		data = params[:save]

		if !File.exist?(dir)
			Dir.mkdir(dir)
		end

		File.open("#{dir}/#{data['userfile'].original_filename}", 'w') {|f|
			f.write(data['userfile'].read)
		}

	end

end
