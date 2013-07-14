package sample

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

When(~/"(.*)" へ "(.*)" を入力/) { name, value ->
	tester.byName(name).sendKeys(value)
}

Then(~'ログイン済みとなる') { ->
	assert tester.byXpath("//span[@class = 'userPanel' and a = 'ログアウト']") != null
}

