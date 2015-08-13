@Grab('org.functionaljava:functionaljava:4.4')
import fj.F
import fj.data.Reader

def sampleConf = [
	url: 'aaa',
	user: 'bbb'
]

Reader<Map, String> r = Reader.unit({ conf -> 
	"result:${conf.url}"
} as F)

def r2 = r.flatMap({ str ->
	Reader.unit({ conf -> "${conf.user}|${str}" } as F)
} as F)

println r.f(sampleConf)

println r2.f(sampleConf)

println r2.andThen({ str -> str.length() } as F).f(sampleConf)

