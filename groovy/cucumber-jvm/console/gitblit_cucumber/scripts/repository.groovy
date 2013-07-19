package sample

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

When(~'リポジトリをクリック') { ->
	tester.link('リポジトリ').click()
}

Then(~'リポジトリページを表示') { ->
	assert tester.url == "${TOP_URL}repositories/"
}
