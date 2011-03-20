package fits.sample

scenario "initialize", {
	given "Book", {
		b = new Book()
	}
	when ""
	then "title is null", {
		b.title.shouldBe null
	}
	and
	then "comments is not null", {
		b.comments.shouldNotBe null
	}
	and
	then "comments is empty", {
		b.comments.size.shouldBe 0
	}
}

scenario "set title", {
	given "Book", {
		b = new Book()
	}
	when "set title 'test'", {
		b.title = "test"
	}
	then "title is 'test'", {
		b.title.shouldBeEqual("test")
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
