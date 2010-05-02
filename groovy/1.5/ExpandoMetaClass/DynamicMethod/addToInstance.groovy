
class Data {
	String name  = "AAA"
}

def d = new Data()

def updateName = "Bbb"


def emc = new ExpandoMetaClass(Data.class, false)

emc."changeNameTo${updateName}" = {
	delegate.name = updateName
}

//•K{
emc.initialize()

d.metaClass = emc
d.changeNameToBbb()

println d.name

//ƒGƒ‰[‚ª”­¶
//new Data().changeNameToBbb()

