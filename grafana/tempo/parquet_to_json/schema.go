package main

type Attribute struct {
	Key         string   `parquet:",snappy,dict"`
	Value       *string  `parquet:",dict,snappy,optional"`
	ValueInt    *int64   `parquet:",snappy,optional"`
	ValueDouble *float64 `parquet:",snappy,optional"`
	ValueBool   *bool    `parquet:",snappy,optional"`
	ValueKVList string   `parquet:",snappy,optional"`
	ValueArray  string   `parquet:",snappy,optional"`
}

type EventAttribute struct {
	Key   string `parquet:",snappy,dict"`
	Value []byte `parquet:",snappy"`
}

type Event struct {
	TimeUnixNano           uint64           `parquet:",delta"`
	Name                   string           `parquet:",snappy"`
	Attrs                  []EventAttribute `parquet:""`
	DroppedAttributesCount int32            `parquet:",snappy,delta"`
}

type Span struct {
	ID                     []byte      `parquet:","`
	Name                   string      `parquet:",snappy,dict"`
	Kind                   int         `parquet:",delta"`
	ParentSpanID           []byte      `parquet:","`
	TraceState             string      `parquet:",snappy"`
	StartUnixNanos         uint64      `parquet:",delta"`
	EndUnixNanos           uint64      `parquet:",delta"`
	StatusCode             int         `parquet:",delta"`
	StatusMessage          string      `parquet:",snappy"`
	Attrs                  []Attribute `parquet:""`
	DroppedAttributesCount int32       `parquet:",snappy"`
	Events                 []Event     `parquet:""`
	DroppedEventsCount     int32       `parquet:",snappy"`
	Links                  []byte      `parquet:",snappy"`
	DroppedLinksCount      int32       `parquet:",snappy"`
	HttpMethod             *string     `parquet:",snappy,optional,dict"`
	HttpUrl                *string     `parquet:",snappy,optional,dict"`
	HttpStatusCode         *int64      `parquet:",snappy,optional"`
}

type Scope struct {
	Name    string `parquet:",snappy,dict"`
	Version string `parquet:",snappy,dict"`
}

type ScopeSpan struct {
	Scope Scope  `parquet:"il"`
	Spans []Span `parquet:""`
}

type Resource struct {
	Attrs []Attribute

	ServiceName      string  `parquet:",snappy,dict"`
	Cluster          *string `parquet:",snappy,optional,dict"`
	Namespace        *string `parquet:",snappy,optional,dict"`
	Pod              *string `parquet:",snappy,optional,dict"`
	Container        *string `parquet:",snappy,optional,dict"`
	K8sClusterName   *string `parquet:",snappy,optional,dict"`
	K8sNamespaceName *string `parquet:",snappy,optional,dict"`
	K8sPodName       *string `parquet:",snappy,optional,dict"`
	K8sContainerName *string `parquet:",snappy,optional,dict"`
}

type ResourceSpans struct {
	Resource   Resource    `parquet:""`
	ScopeSpans []ScopeSpan `parquet:"ils"`
}

type Trace struct {
	TraceID           []byte          `parquet:""`
	ResourceSpans     []ResourceSpans `parquet:"rs"`
	TraceIDText       string          `parquet:",snappy"`
	StartTimeUnixNano uint64          `parquet:",delta"`
	EndTimeUnixNano   uint64          `parquet:",delta"`
	DurationNanos     uint64          `parquet:",delta"`
	RootServiceName   string          `parquet:",dict"`
	RootSpanName      string          `parquet:",dict"`
}
