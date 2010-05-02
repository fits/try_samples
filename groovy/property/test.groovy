
class Data {
    def id  //プロパティ
    String name  //プロパティ
    public info  //public なフィールド（アクセサメソッド無し）
}

data = new Data()
data.id = "a1"
data.name = "てすと"

println "id = ${data.id}, name = ${data.name}"
