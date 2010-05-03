# coding: utf-8
#
# 関数デコレーターのサンプル

def invoke(func):
    print "--- before"
    func()
    print "--- after"

    def nothing(): pass

    return nothing


@invoke
def test():
    print "exe test"


test()
