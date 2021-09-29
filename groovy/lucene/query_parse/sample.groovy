@Grab('org.apache.lucene:lucene-queryparser:8.9.0')
import org.apache.lucene.index.Term
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser
import org.apache.lucene.search.*

def parser = new StandardQueryParser()

def visitor = [
    consumeTerms: { Query q, Term... terms ->
        println("*** consumeTerms = ${q}")

        terms.each {
            println("type = ${it.class.simpleName}, field = ${it.field()}, text = ${it.text()}")
        }
    },
    visitLeaf: { Query q ->
        println("*** visitLeaf = ${q}")

        switch (q) {
            case WildcardQuery:
            case PrefixQuery:
                println("type = ${q.class.simpleName}, field = ${q.term.field()}, text = ${q.term.text()}")
                break

            case TermRangeQuery:
                println("type = ${q.class.simpleName}, field = ${q.field}, lower = ${q.lowerTerm?.utf8ToString()}, upper = ${q.upperTerm?.utf8ToString()}")
                break

            default:
                println(q.class)
        }
    }
] as QueryVisitor

def parseQuery = { qstr ->
    println "----- ${qstr} -----"

    parser.parse(qstr, '').visit(visitor)

    println ''
}

parseQuery 'test:a12'
parseQuery '-test:a12'
parseQuery 'test:a1*'
parseQuery 'test:a1*b'
parseQuery 'test:a1 OR test:b2'
parseQuery 'test:[1 TO 8] OR test:[100 TO *]'
