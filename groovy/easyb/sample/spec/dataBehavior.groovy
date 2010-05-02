
import sample.Data

before "Data ‚Ì‰Šú‰»", {
    data = new Data("test")
}

it "–¼‘O‚ğ‚Á‚Ä‚¢‚é", {
    data.getName().shouldBe "test"
}

it "–¼‘O‚Í•ÏX‚Å‚«‚È‚¢", {
    ensureThrows(Exception) {
        data.setName("aaa")
    }
}