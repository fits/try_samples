# coding: utf-8

f = open('test.txt', 'rb')

try:
    for line in f:
        print unicode(line, 'shift_jis')
finally:
    f.close()

