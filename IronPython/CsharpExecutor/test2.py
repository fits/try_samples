# coding: shift_jis

class Course:
	def __init__(self, name):
		self.name = name

def get_courses():
	return [Course("test1"), Course("テスト2"), Course("てすとabc")]

from System import *
from System.Collections import *

def get_courses2():
	result = Hashtable()
	result["1"] = "test1"
	result["2"] = "テスト2"
	return result