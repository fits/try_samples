package sample

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

When(~'リポジトリ作成リンクをクリックする') { ->
	tester.link('リポジトリ作成').click()
}

Then(~'リポジトリ作成ページを表示') { ->
	assert tester.byXpath("//span[. = 'リポジトリ作成']") != null
}

