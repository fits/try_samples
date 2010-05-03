# coding: shift_jis

from System import *
from System.Collections.Generic import *

import clr
clr.AddReference("CsharpExecutor")
from CsharpExecutor import *

class StoreImpl(IStore):

    def __init__(self): pass

    def GetCourses(self):
        result = List[Course]()
        result.Add(Course("1", "test1"))
        result.Add(Course("2", "テスト2"))
        return result
