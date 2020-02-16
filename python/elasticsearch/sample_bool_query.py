
from elasticsearch import Elasticsearch

es = Elasticsearch()

q = {
    'query': {
        'bool': {
            'must': [
                {'term': {'item.code.keyword': 'item-2'}}, 
                {'terms': {'location.code.keyword': ['loc-2', 'loc-3']}}
            ]
        }
    },
    'sort': [ 'location.code.keyword' ]
}

res = es.search(index = 'samples', body = q)

for r in res['hits']['hits']:
    print(r['_source'])
