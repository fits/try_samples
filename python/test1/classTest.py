
class Course:

    def __init__(self, id, title):
        self.id = id
        self.title = title

    def check(self):
        print "id= %s, title= %s" % (self.id, self.title)
        self.check2()

    def check2(self):
        print "check2"

    @staticmethod
    def abc():
        print "call abc"

    @classmethod
    def clsabc(cls):
        print "call clsabc = %s" % cls


cs = Course(1, "test")
cs.check()
cs.abc()
Course.abc()

cs.clsabc()
Course.clsabc()

