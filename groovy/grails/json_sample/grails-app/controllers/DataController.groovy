
import grails.converters.*

class DataController {

    def index = { }

    def listup = {
        def list = [
            new Data(name: "テストデータ", date: new Date()),
            new Data(name: "test"),
            new Data(name: "aaaa", point: 100)
        ]

        render new JSON(list)
    }

    def listupxml = {
        def list = [
            new PersonalData(no: 1, name: "test", tel: "1111-2222-333", mail: "aaa@bbb.ccc"),
            new PersonalData(no: 2, name: "test2", tel: "1111-2222-333", mail: "aaa@bbb.ccc"),
            new PersonalData(no: 3, name: "test3", tel: "001-2222-333", mail: "ccc@bbb.ccc")
        ]

        render new XML(list)
    }
}

    class PersonalData {
        int no;
        String name;
        String address;
        String tel;
        String mail;
    }

