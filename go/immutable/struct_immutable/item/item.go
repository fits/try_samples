package item

type Item interface {
	Name() string
	Value() int32
}

type item struct {
	name string
	value int32
}

func (i item) Name() string {
	return i.name
}

func (i item) Value() int32 {
	return i.value
}

func NewItem(name string, value int32) Item {
	return item{name, value}
}
