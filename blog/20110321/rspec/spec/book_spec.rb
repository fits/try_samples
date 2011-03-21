require "java"

module Fits
	include_package "fits.sample"
end

describe "Book" do
	context "‰Šúó‘Ô" do
		before do
			@b = Fits::Book.new
		end

		it "comments ‚Í nil ‚Å‚Í‚È‚¢" do
			@b.comments.should_not be_nil
		end

		it "comments ‚Í‹ó" do
			@b.comments.size.should == 0
		end
	end

	context "Comment ‚ğ’Ç‰Á" do
		before do
			@b = Fits::Book.new
			@b.comments.add(Fits::Comment.new)
		end

		it "Comment ‚ª’Ç‰Á‚³‚ê‚Ä‚¢‚é" do
			@b.comments.size.should == 1
		end
	end
end
