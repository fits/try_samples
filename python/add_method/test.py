# coding: utf-8

class Test:
    def check_test(self):
        print "test"

def test(self):
    print "test2"

te = Test()
te.check_test()

import new

te.check_test2 = new.instancemethod(test, te, Test)
te.check_test2()

del te.check_test2
