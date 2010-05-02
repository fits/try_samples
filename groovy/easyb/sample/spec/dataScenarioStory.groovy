import sample.Data

scenario "名前を持っている", {
    given "Data の初期化", {
        data = new Data("test")
    }

    then "コンストラクタで設定された値が名前となるべき", {
        data.getName().shouldBe "test"
    }
}

scenario "名前は変更できない", {
    given "Data の初期化", {
        data = new Data("test")
    }

    when "名前を変更する", {
        setname = {
            data.setName("aaa")
        }
    }

    then "例外が発生すべき", {
        ensureThrows(Exception) {
            setname()
        }
    }
}
