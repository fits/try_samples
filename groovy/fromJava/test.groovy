
class TestContainer implements Container{

    @Property table

    def TestContainer() {
        table = new HashMap()

        table["test"] = "test data"
        table["data"] = new SimpleData("‚Ä‚·‚Æ", 10)

    }

    def Object getComponent(String name) {
        return table[name]
    }

}