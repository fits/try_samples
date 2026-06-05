
struct Data[T: Equatable & ImplicitlyCopyable](Equatable, Writable):
    var name: StaticString
    var value: Self.T

    def __init__(out self, name: StaticString, value: Self.T):
        self.name = name
        self.value = value

def main():
    d1 = Data('data1', 10)
    print(d1)
    print(d1 == Data('data1', 10))
    print(d1 == Data('data1', 5))

    d2 = Data('data2', 10.0)
    print(d2)
    print(d2 == Data('data2', 10.0))
    print(d2 == Data('data2', 5.0))

    # print(d1 == d2) # compile error

    d3 = Data('data3', ('a', 1))
    print(d3)
    print(d3 == Data('data3', ('a', 1)))
    print(d3 == Data('data3', ('a', 2)))

    # d4 = Data('data4', [1, 2]) # compile error ('List[Int]' does not conform to trait 'Equatable & ImplicitlyCopyable')
