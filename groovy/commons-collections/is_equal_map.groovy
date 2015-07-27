@Grab('org.apache.commons:commons-collections4:4.0')
import org.apache.commons.collections4.CollectionUtils

def m1 = ['a': 2, 'b': 3]
def m2 = ['b': 3, 'a': 2]
// true
println CollectionUtils.isEqualCollection(m1.entrySet(), m2.entrySet())

def m3 = ['a': 2, 'b': 4]
// false
println CollectionUtils.isEqualCollection(m1.entrySet(), m3.entrySet())

def m4 = ['a': 2]
// false
println CollectionUtils.isEqualCollection(m1.entrySet(), m4.entrySet())

def m5 = ['a': 2, 'b': 4, 'c': 1]
// false
println CollectionUtils.isEqualCollection(m1.entrySet(), m5.entrySet())
