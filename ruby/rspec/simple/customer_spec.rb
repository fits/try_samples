
describe "顧客データ" do

	before :all do
		puts "振る舞いの実行前"
	end

	before do
		puts "サンプルの実行前"
	end

	after do
		puts "サンプルの実行後"
	end

	after :all do
		puts "振る舞いの実行後"
	end

	it "sample1" do
		puts "sample1"
		"".length.should == 0
	end

	it "sample2" do
		puts "sample2"
		"1".length.should_not == 0
	end
end