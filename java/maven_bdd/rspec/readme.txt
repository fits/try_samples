[RSpec sample 実行時の注意点]

・target/rspec-runner.rb の 48行目をコメントアウトした状態で
	実行する必要あり（そうしないとエラーが発生）

	（例）47～49行目
		::RSpec.configure do |config|
		#  config.formatter = ::MultiFormatter
		end

