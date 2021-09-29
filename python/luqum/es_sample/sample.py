from luqum.parser import parser
from luqum.elasticsearch import ElasticsearchQueryBuilder

es_builder = ElasticsearchQueryBuilder(not_analyzed_fields=["test", "num"])

r = parser.parse('(test:a1-* OR test:b2) AND (num:{1 TO 5} OR num:[100 TO *])')

print(repr(r))

q = es_builder(r)

print(q)
