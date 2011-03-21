package fits.sample

scenario "init state", {
	given "Book", {
		b = new Book()
	}
	when ""
	then "comments is not null", {
		b.comments.shouldNotBe null
	}
	and
	then "comments is empty", {
		b.comments.size.shouldBe 0
	}
}

scenario "add Comment", {
	given "Book", {
		b = new Book()
	}
	when "add Comment", {
		b.comments.add(new Comment())
	}
	then "added Comment", {
		b.comments.size.shouldBe 1
	}
}
