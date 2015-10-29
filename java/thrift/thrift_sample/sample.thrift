namespace java sample

struct Data {
	1: required string name,
	2: required i64 value,
}

service SampleService {
	oneway void add(1: Data data),
	list<Data> findAll(),
}